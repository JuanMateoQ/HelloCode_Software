package GestionAprendizaje_Modulo.Controladores;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class WebController {

    // ===== Config mínima =====
    private static final String UA_MOBILE =
            "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/120.0.0.0 Mobile Safari/537.36";

    // Bloquea media con src data:/blob: (inicial + dinámico) de forma simple
    private static final String JS_BLOCK_MEDIA =
            "(function(){try{"
                    + "if(window.__JFX_MEDIA_GUARD)return; window.__JFX_MEDIA_GUARD=1;"
                    + "const bad=u=>typeof u==='string'&&(u.startsWith('data:')||u.startsWith('blob:'));"
                    + "const clean=m=>{try{if(!m)return; m.pause&&m.pause(); m.removeAttribute&&m.removeAttribute('src'); try{m.srcObject=null;}catch(e){} m.load&&m.load(); m.autoplay=false; m.preload='none';}catch(e){}};"
                    + "const scrub=m=>{try{var s=''+(m.currentSrc||m.src||''); if(bad(s)) clean(m);}catch(e){}};"
                    + "var scan=function(root){try{(root.querySelectorAll?root:document).querySelectorAll('video,audio').forEach(scrub);}catch(e){}};"
                    + "document.addEventListener('DOMContentLoaded',function(){scan(document);},{once:true});"
                    + "scan(document);"
                    + "new MutationObserver(function(ms){ms.forEach(function(m){(m.addedNodes||[]).forEach(function(n){try{if(n&&n.tagName&&(n.tagName==='VIDEO'||n.tagName==='AUDIO'))scrub(n); if(n&&n.querySelectorAll)scan(n);}catch(e){}});});}).observe(document.documentElement,{childList:true,subtree:true});"
                    + "}catch(e){}})();";

    static {
        // Silencia los warnings del reproductor interno
        try { Logger.getLogger("com.sun.javafx.webkit.prism.WCMediaPlayerImpl").setLevel(Level.OFF); } catch (Exception ignored) {}
    }

    @FXML private WebView webView;
    @FXML private Label titleLabel;
    @FXML private Button backButton, forwardButton;
    @FXML private ProgressBar progressBar;

    private String currentUrl = "";

    @FXML
    private void initialize() {
        WebEngine engine = webView.getEngine();
        engine.setUserAgent(UA_MOBILE);

        // Título y progreso
        engine.titleProperty().addListener((o, ov, nv) -> titleLabel.setText((nv != null && !nv.isBlank()) ? nv : currentUrl));
        progressBar.progressProperty().bind(engine.getLoadWorker().progressProperty());
        progressBar.visibleProperty().bind(engine.getLoadWorker().runningProperty());
        progressBar.managedProperty().bind(progressBar.visibleProperty());

        // Errores: fallback de título y, si era video, abrir afuera como último recurso
        engine.getLoadWorker().exceptionProperty().addListener((o, oldEx, ex) -> {
            if (isBlank(titleLabel.getText())) Platform.runLater(() -> titleLabel.setText(currentUrl == null ? "Error al cargar" : currentUrl));
            if (ex != null && esYouTube(currentUrl)) abrirExterno(currentUrl);
        });

        // Inyección temprana del guardián de medios al crearse el documento
        engine.documentProperty().addListener((obs, od, nd) -> { if (nd != null) runJS(JS_BLOCK_MEDIA); });
        engine.getLoadWorker().stateProperty().addListener((o2, oldSt, st) -> { if (st == Worker.State.SUCCEEDED) runJS(JS_BLOCK_MEDIA); });
        engine.documentProperty().addListener((obs, oldDoc, newDoc) -> { if (newDoc != null) runJS(JS_BLOCK_MEDIA); });
        engine.getLoadWorker().stateProperty().addListener((o2, oldSt, st) -> {
            if (st == Worker.State.SUCCEEDED) runJS(JS_BLOCK_MEDIA);
        });

        // Cierre ventana: parar medios y limpiar
        webView.sceneProperty().addListener((o, os, ns) -> {
            if (ns != null) ns.windowProperty().addListener((o2, ow, nw) -> {
                if (nw != null) nw.setOnHidden(e -> { detenerMedios(); cargarEnBlanco(); });
            });
        });

        actualizarBotones();
    }

    // ===== API pública (mismos nombres) =====
    public void cargarUrl(String url) { loadUrl(url); }

    public void loadUrl(String url) {
        currentUrl = normalizarUrl(url);
        cancelarCarga();

        if (esYouTube(currentUrl)) {
            String embed = youTubeEmbed(currentUrl);
            webView.getEngine().loadContent(wrapperIFrameFull(embed), "text/html");
            titleLabel.setText("YouTube");
            return;
        }

        if (esPdf(currentUrl)) {
            webView.getEngine().load("https://docs.google.com/gview?embedded=true&url=" + urlEncode(currentUrl));
        } else {
            webView.getEngine().load(currentUrl);
        }
    }

    public void establecerTituloVentanaPorDefecto(String t) { setWindowTitleFallback(t); }
    public void setWindowTitleFallback(String t) {
        if (isBlank(titleLabel.getText())) titleLabel.setText(t);
    }

    // ===== Handlers @FXML =====
    @FXML private void irAtras() {
        detenerMedios();
        WebHistory h = webView.getEngine().getHistory();
        if (h.getCurrentIndex() > 0) h.go(-1); else cargarEnBlanco();
    }

    @FXML private void irAdelante() {
        detenerMedios();
        WebHistory h = webView.getEngine().getHistory();
        if (h.getCurrentIndex() < h.getEntries().size() - 1) h.go(1);
    }

    @FXML private void recargar() { detenerMedios(); webView.getEngine().reload(); }

    @FXML private void abrirEnNavegadorExterno() {
        String u = webView.getEngine().getLocation();
        abrirExterno(isBlank(u) ? currentUrl : u);
    }

    @FXML private void cerrarVentana() {
        detenerMedios(); cargarEnBlanco();
        var scene = webView.getScene();
        if (scene != null && scene.getWindow() != null) ((javafx.stage.Stage) scene.getWindow()).close();
    }

    // ===== Utilidades compactas =====
    private void actualizarBotones() {
        Platform.runLater(() -> {
            WebHistory h = webView.getEngine().getHistory();
            int i = h.getCurrentIndex(), n = h.getEntries().size();
            backButton.setDisable(i <= 0);
            forwardButton.setDisable(i >= n - 1);
        });
    }

    private void cancelarCarga() { try { webView.getEngine().getLoadWorker().cancel(); } catch (Exception ignored) {} }
    private void cargarEnBlanco() { try { webView.getEngine().load("about:blank"); } catch (Exception ignored) {} }

    private void detenerMedios() {
        runJS("(function(){try{document.querySelectorAll('video,audio').forEach(function(m){try{m.pause&&m.pause();m.removeAttribute&&m.removeAttribute('src');try{m.srcObject=null;}catch(e){}m.load&&m.load();}catch(e){}});}catch(e){}})();");
        cancelarCarga();
    }

    private void runJS(String js) { try { webView.getEngine().executeScript(js); } catch (Exception ignored) {} }

    private void abrirExterno(String url) {
        try { if (!isBlank(url) && Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI(url)); } catch (Exception ignored) {}
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    private String normalizarUrl(String url) {
        if (url == null) return "";
        String t = url.trim().replace(" ", "%20");
        return Pattern.compile("^(?i)(https?|ftp)://").matcher(t).find() ? t : "https://" + t;
    }

    private boolean esPdf(String url) {
        String l = url.toLowerCase();
        return l.contains(".pdf") || l.contains("content-type=application/pdf");
    }

    private boolean esYouTube(String url) {
        String u = url.toLowerCase();
        return u.contains("youtube.com") || u.contains("youtu.be/");
    }

    private String youTubeEmbed(String url) {
        try {
            if (url.contains("youtube.com/watch?v=")) {
                String id = url.substring(url.indexOf("watch?v=") + 8);
                int amp = id.indexOf('&'); if (amp >= 0) id = id.substring(0, amp);
                return "https://www.youtube-nocookie.com/embed/" + id + "?rel=0&modestbranding=1&playsinline=1";
            }
            if (url.contains("youtu.be/")) {
                String id = url.substring(url.indexOf("youtu.be/") + 9);
                int q = id.indexOf('?'); if (q >= 0) id = id.substring(0, q);
                return "https://www.youtube-nocookie.com/embed/" + id + "?rel=0&modestbranding=1&playsinline=1";
            }
            return url; // por si acaso llega ya en /embed/
        } catch (Exception e) {
            return url;
        }
    }

    private String wrapperIFrameFull(String embedUrl) {
        return "<!doctype html><html><head><meta charset='utf-8'/>"
                + "<meta name='viewport' content='width=device-width,initial-scale=1'/>"
                + "<style>html,body{height:100%;margin:0;background:#000}"
                + "iframe{width:100%;height:100%;border:0;display:block}</style>"
                + "</head><body>"
                + "<iframe src='" + escapeHtml(embedUrl) + "' "
                + "allow='accelerometer; autoplay; encrypted-media; picture-in-picture' "
                + "referrerpolicy='strict-origin-when-cross-origin' allowfullscreen></iframe>"
                + "</body></html>";
    }

    private static String escapeHtml(String s) {
        return isBlank(s) ? "" : s.replace("&","&amp;").replace("\"","&quot;").replace("<","&lt;").replace(">","&gt;");
    }

    private static String urlEncode(String s) {
        try { return URLEncoder.encode(s, StandardCharsets.UTF_8.toString()); }
        catch (Exception e) { return s; }
    }}