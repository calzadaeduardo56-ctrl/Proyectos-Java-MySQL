
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.time.Duration;
import java.util.function.Consumer;

public class GroqClient {

    private static final String BASE_URL
            = "https://api.groq.com/openai/v1/chat/completions";

    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient http;
    private final String apiKey;
    private final String model;

    public GroqClient() {
        this(
                System.getenv("GROQ_API_KEY"),
                "openai/gpt-oss-120b"
        );
    }

    public GroqClient(String apiKey, String model) {

        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException(
                    "Falta GROQ_API_KEY en variables de entorno."
            );
        }

        this.apiKey = apiKey;
        this.model = model;

        this.http = new OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(120))
                .connectTimeout(Duration.ofSeconds(20))
                .readTimeout(Duration.ofSeconds(120))
                .build();
    }

    /**
     * Llama al endpoint /chat/completions en modo bloqueante.
     */
    public String chat(
            String systemInstruction,
            String userPrompt,
            double temperature,
            int maxTokens) throws IOException {

        JSONObject body = new JSONObject();

        body.put("model", model);
        body.put("temperature", temperature);
        body.put("max_tokens", maxTokens);

        JSONArray messages = new JSONArray();

        messages.put(
                new JSONObject()
                        .put("role", "system")
                        .put("content", systemInstruction)
        );

        messages.put(
                new JSONObject()
                        .put("role", "user")
                        .put("content", userPrompt)
        );

        body.put("messages", messages);

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(
                        RequestBody.create(
                                body.toString(),
                                JSON
                        )
                )
                .addHeader(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .addHeader(
                        "Content-Type",
                        "application/json"
                )
                .build();

        try (Response response = http.newCall(request).execute()) {

            String resp
                    = response.body() != null
                    ? response.body().string()
                    : "";

            if (!response.isSuccessful()) {

                throw new IOException(
                        "Error "
                        + response.code()
                        + ": "
                        + resp
                );
            }

            JSONObject json = new JSONObject(resp);

            JSONArray choices
                    = json.getJSONArray("choices");

            return choices
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }
    }

    /**
     * Streaming en tiempo real.
     */
    public void chatStream(
            String systemInstruction,
            String userPrompt,
            double temperature,
            int maxTokens,
            Consumer<String> onDelta,
            Runnable onComplete,
            Consumer<Throwable> onError) {

        JSONObject body = new JSONObject();

        body.put("model", model);
        body.put("temperature", temperature);
        body.put("max_tokens", maxTokens);
        body.put("stream", true);

        JSONArray messages = new JSONArray();

        messages.put(
                new JSONObject()
                        .put("role", "system")
                        .put("content", systemInstruction)
        );

        messages.put(
                new JSONObject()
                        .put("role", "user")
                        .put("content", userPrompt)
        );

        body.put("messages", messages);

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(
                        RequestBody.create(
                                body.toString(),
                                JSON
                        )
                )
                .addHeader(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .addHeader(
                        "Content-Type",
                        "application/json"
                )
                .build();

        EventSource.Factory factory
                = EventSources.createFactory(http);

        factory.newEventSource(
                request,
                new EventSourceListener() {

            @Override
            public void onEvent(
                    EventSource eventSource,
                    String id,
                    String type,
                    String data) {

                /*
                         * Groq indica que terminó el streaming.
                 */
                if ("[DONE]".equals(data.trim())) {

                    if (onComplete != null) {
                        onComplete.run();
                    }

                    eventSource.cancel();
                    return;
                }

                try {

                    JSONObject json
                            = new JSONObject(data);

                    JSONArray choices
                            = json.getJSONArray("choices");

                    if (choices.length() == 0) {
                        return;
                    }

                    JSONObject delta
                            = choices
                                    .getJSONObject(0)
                                    .getJSONObject("delta");

                    /*
                             * Obtener solamente el texto generado.
                     */
                    if (delta.has("content")
                            && !delta.isNull("content")) {

                        String token
                                = delta.getString("content");

                        if (onDelta != null) {
                            onDelta.accept(token);
                        }
                    }

                } catch (Exception e) {

                    if (onError != null) {
                        onError.accept(e);
                    }
                }
            }

            @Override
            public void onFailure(
                    EventSource eventSource,
                    Throwable t,
                    Response response) {

                if (onError == null) {
                    return;
                }

                // Error de conexión
                if (t != null) {
                    onError.accept(t);
                    return;
                }

                // Error HTTP
                if (response != null) {

                    String mensaje = "Error HTTP " + response.code();

                    try {

                        if (response.body() != null) {

                            String cuerpo = response.body().string();

                            if (!cuerpo.isEmpty()) {
                                mensaje += "\n\nRespuesta de Groq:\n" + cuerpo;
                            }
                        }

                    } catch (Exception ex) {

                        mensaje += "\nNo fue posible leer la respuesta del servidor.";
                    }

                    onError.accept(new IOException(mensaje));
                    return;
                }

                // Error desconocido
                onError.accept(
                        new IOException(
                                "La conexión con Groq terminó inesperadamente."
                        )
                );
            }
        }
        );
    }

    /**
     * Plantilla para guías de soporte técnico.
     */
    public String buildSolutionPrompt(String tema) {

        return """
    Genera una guía técnica COMPLETA, clara y accionable en ESPAÑOL
    sobre el siguiente problema o tema:

    "%s"

    OBJETIVO:
    La guía será utilizada por un técnico de soporte para resolver
    el problema de forma práctica. Debe explicar qué hacer, en qué
    orden hacerlo y cómo comprobar que la solución funcionó.

    ESTRUCTURA OBLIGATORIA:

    # 1. Resumen
    Explica brevemente cuál es el problema, qué se va a realizar
    y cuál es el resultado esperado.

    # 2. Requisitos previos
    Indica únicamente los equipos, permisos, versiones, conexiones
    o programas que realmente sean necesarios para realizar el procedimiento.

    # 3. Materiales y software
    Presenta los elementos necesarios mediante una lista con viñetas.
    Si existen alternativas, indícalas dentro de la misma viñeta.

    IMPORTANTE:
    - NO crear tablas para esta sección.
    - NO utilizar columnas de "Elemento", "Alternativas", "Categoría"
      ni estructuras similares.
    - No incluir materiales que no sean necesarios para el problema.

    # 4. Procedimiento
    Explica el procedimiento paso a paso.

    Cada paso debe:
    - Tener una acción concreta.
    - Explicar dónde realizarla cuando sea necesario.
    - Incluir comandos o configuraciones exactas cuando correspondan.
    - Explicar brevemente qué debe observar el técnico.

    Utiliza listas numeradas.
    NO conviertas los pasos en tablas.

    # 5. Verificación
    Explica cómo comprobar que la solución funcionó.

    Presenta las comprobaciones como una lista de acciones.
    Para cada comprobación indica qué resultado debería obtener
    el técnico.

    NO utilizar tablas.

    # 6. Solución de problemas
    Describe los problemas más probables que pueden aparecer durante
    el procedimiento.

    Para cada problema utiliza este formato:

    **Problema:** descripción breve.
    **Causa posible:** explicación.
    **Solución:** acción concreta para corregirlo.

    NO utilizar tablas.

    # 7. Comandos o código
    Incluye comandos, configuraciones o código solamente cuando sean
    necesarios para resolver el problema.

    Utiliza bloques de código Markdown cuando corresponda.

    # 8. Buenas prácticas y seguridad
    Incluye recomendaciones importantes para evitar errores,
    pérdida de información, problemas de configuración o riesgos
    de seguridad.

    # 9. Resultado esperado
    Finaliza explicando brevemente cómo debe quedar el sistema
    después de completar correctamente el procedimiento.

    REGLAS IMPORTANTES:

    - Responde SIEMPRE en español.
    - Utiliza Markdown sencillo.
    - Prioriza listas, párrafos y pasos numerados.
    - NO utilices tablas Markdown.
    - NO generes estructuras con columnas.
    - NO agrupes la información en tablas de "Categoría / Elemento /
      Alternativas".
    - NO generes tablas para verificación.
    - NO generes tablas para solución de problemas.
    - Evita información repetida.
    - No inventes hardware, modelos, direcciones IP, comandos,
      versiones o configuraciones específicas si no son necesarias.
    - Si falta información, utiliza el escenario más común y
      aclara brevemente el supuesto.
    - No hagas preguntas al usuario.
    - Proporciona una solución práctica que un técnico pueda seguir.
    - Mantén un lenguaje profesional, claro y conciso.
    - No agregues una introducción innecesariamente extensa.

    IMPORTANTE SOBRE EL FORMATO:
    La respuesta debe parecer una guía escrita por un técnico
    para otro técnico, no una tabla de documentación.

    """.formatted(tema);
    }

    /**
     * Genera una guía completa en modo bloqueante.
     */
    public String generarGuia(String tema) throws IOException {

        String system = """
            Eres un experto en soporte técnico multi-plataforma.
            Escribes guías paso a paso, concretas y verificables,
            pensadas para técnicos de campo.

            Respondes SIEMPRE en español
            y en formato Markdown.
        """;

        String user = buildSolutionPrompt(tema);

        return chat(
                system,
                user,
                0.2,
                2500
        );
    }
}
