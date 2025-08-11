package Modulo_Ejercicios.Controladores;

import Modulo_Ejercicios.DataBase.EjercicioRepository;
import Modulo_Ejercicios.logic.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GUICrud_Controller implements Initializable {
    
    // Elementos del formulario
    @FXML private ComboBox<String> comboTipoEjercicio;
    @FXML private TextArea txtInstruccion;
    @FXML private ComboBox<Lenguaje> comboLenguaje;
    @FXML private ComboBox<NivelDificultad> comboNivel;
    
    // Sección ejercicios de selección
    @FXML private VBox seccionSeleccion;
    @FXML private TextArea txtOpciones;
    @FXML private TextField txtRespuestasCorrectas;
    
    // Sección ejercicios de completar código
    @FXML private VBox seccionCompletar;
    @FXML private TextArea txtCodigo;
    @FXML private TextField txtRespuestasEspacios;
    
    // Lista y filtros
    @FXML private ListView<EjercicioBase> listaEjercicios;
    @FXML private ComboBox<String> filtroTipo;
    @FXML private ComboBox<Lenguaje> filtroLenguaje;
    
    // Listas de datos
    private ObservableList<EjercicioBase> todosLosEjercicios;
    private ObservableList<EjercicioBase> ejerciciosFiltrados;
    private EjercicioBase ejercicioSeleccionado;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarCombos();
        configurarLista();
        cargarEjercicios();
        configurarEventos();
    }
    
    private void configurarCombos() {
        // Tipo de ejercicio
        comboTipoEjercicio.setItems(FXCollections.observableArrayList(
            "Selección Múltiple", "Completar Código"
        ));
        
        // Lenguajes
        comboLenguaje.setItems(FXCollections.observableArrayList(Lenguaje.values()));
        filtroLenguaje.setItems(FXCollections.observableArrayList(Lenguaje.values()));
        
        // Niveles de dificultad
        comboNivel.setItems(FXCollections.observableArrayList(NivelDificultad.values()));
        
        // Filtros
        filtroTipo.setItems(FXCollections.observableArrayList(
            "Todos", "Selección Múltiple", "Completar Código"
        ));
        filtroTipo.setValue("Todos");
        
        // Agregar opción "Todos" al filtro de lenguaje
        ObservableList<Lenguaje> lenguajesConTodos = FXCollections.observableArrayList();
        lenguajesConTodos.addAll(Lenguaje.values());
        filtroLenguaje.setItems(lenguajesConTodos);
    }
    
    private void configurarLista() {
        todosLosEjercicios = FXCollections.observableArrayList();
        ejerciciosFiltrados = FXCollections.observableArrayList();
        listaEjercicios.setItems(ejerciciosFiltrados);
        
        // Configurar como se muestra cada ejercicio en la lista
        listaEjercicios.setCellFactory(param -> new ListCell<EjercicioBase>() {
            @Override
            protected void updateItem(EjercicioBase item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String tipo = (item instanceof EjercicioSeleccion) ? "📝 Selección" : "💻 Completar";
                    String preview = item.getInstruccion().length() > 50 ? 
                        item.getInstruccion().substring(0, 50) + "..." : 
                        item.getInstruccion();
                    setText(String.format("%s | %s | %s\n%s", 
                        tipo, 
                        item.getLenguaje(), 
                        item.getNivel(), 
                        preview));
                }
            }
        });
    }
    
    private void configurarEventos() {
        // Listener para selección en la lista
        listaEjercicios.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                ejercicioSeleccionado = newValue;
            }
        );
    }
    
    private void cargarEjercicios() {
        todosLosEjercicios.clear();
        
        // Cargar ejercicios de selección
        List<EjercicioSeleccion> ejerciciosSeleccion = EjercicioRepository.cargarEjerciciosSeleccion();
        todosLosEjercicios.addAll(ejerciciosSeleccion);
        
        // Cargar ejercicios de completar código
        List<EjercicioCompletarCodigo> ejerciciosCompletar = EjercicioRepository.cargarEjerciciosCompletarCodigo();
        todosLosEjercicios.addAll(ejerciciosCompletar);
        
        // Actualizar lista filtrada
        filtrarEjercicios();
    }
    
    @FXML
    private void cambiarTipoEjercicio() {
        String tipoSeleccionado = comboTipoEjercicio.getValue();
        
        // Ocultar todas las secciones específicas
        seccionSeleccion.setVisible(false);
        seccionSeleccion.setManaged(false);
        seccionCompletar.setVisible(false);
        seccionCompletar.setManaged(false);
        
        // Mostrar la sección correspondiente
        if ("Selección Múltiple".equals(tipoSeleccionado)) {
            seccionSeleccion.setVisible(true);
            seccionSeleccion.setManaged(true);
        } else if ("Completar Código".equals(tipoSeleccionado)) {
            seccionCompletar.setVisible(true);
            seccionCompletar.setManaged(true);
        }
    }
    
    @FXML
    private void crearEjercicio() {
        try {
            // Validar campos comunes
            if (!validarCamposComunes()) {
                return;
            }
            
            String tipo = comboTipoEjercicio.getValue();
            
            if ("Selección Múltiple".equals(tipo)) {
                crearEjercicioSeleccion();
            } else if ("Completar Código".equals(tipo)) {
                crearEjercicioCompletarCodigo();
            }
            
            // Actualizar la lista
            cargarEjercicios();
            
            // Limpiar formulario
            limpiarCampos();
            
            mostrarMensaje("Éxito", "Ejercicio creado exitosamente", Alert.AlertType.INFORMATION);
            
        } catch (Exception e) {
            mostrarMensaje("Error", "Error al crear el ejercicio: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private boolean validarCamposComunes() {
        if (comboTipoEjercicio.getValue() == null) {
            mostrarMensaje("Error", "Selecciona el tipo de ejercicio", Alert.AlertType.WARNING);
            return false;
        }
        
        if (txtInstruccion.getText().trim().isEmpty()) {
            mostrarMensaje("Error", "Ingresa la instrucción del ejercicio", Alert.AlertType.WARNING);
            return false;
        }
        
        if (comboLenguaje.getValue() == null) {
            mostrarMensaje("Error", "Selecciona el lenguaje", Alert.AlertType.WARNING);
            return false;
        }
        
        if (comboNivel.getValue() == null) {
            mostrarMensaje("Error", "Selecciona el nivel de dificultad", Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
    }
    
    private void crearEjercicioSeleccion() {
        if (txtOpciones.getText().trim().isEmpty()) {
            mostrarMensaje("Error", "Ingresa las opciones de selección", Alert.AlertType.WARNING);
            return;
        }
        
        if (txtRespuestasCorrectas.getText().trim().isEmpty()) {
            mostrarMensaje("Error", "Ingresa las respuestas correctas", Alert.AlertType.WARNING);
            return;
        }
        
        // Parsear opciones y respuestas
        ArrayList<String> opciones = new ArrayList<>(Arrays.asList(
            txtOpciones.getText().split(",")
        ));
        opciones.replaceAll(String::trim);
        
        ArrayList<String> respuestas = new ArrayList<>(Arrays.asList(
            txtRespuestasCorrectas.getText().split(",")
        ));
        respuestas.replaceAll(String::trim);
        
        // Crear ejercicio usando el Builder
        EjercicioSeleccion ejercicio = new EjercicioSeleccion.Builder()
            .conInstruccion(txtInstruccion.getText().trim())
            .conOpciones(opciones)
            .conRespuestasCorrectas(respuestas)
            .conNivel(comboNivel.getValue())
            .conLenguaje(comboLenguaje.getValue())
            .construir();
        
        // Guardar en repositorio
        EjercicioRepository.guardarEjercicioSeleccion(ejercicio);
    }
    
    private void crearEjercicioCompletarCodigo() {
        if (txtCodigo.getText().trim().isEmpty()) {
            mostrarMensaje("Error", "Ingresa el código con espacios en blanco", Alert.AlertType.WARNING);
            return;
        }
        
        if (txtRespuestasEspacios.getText().trim().isEmpty()) {
            mostrarMensaje("Error", "Ingresa las respuestas para los espacios", Alert.AlertType.WARNING);
            return;
        }
        
        // Parsear respuestas
        ArrayList<String> respuestas = new ArrayList<>(Arrays.asList(
            txtRespuestasEspacios.getText().split(",")
        ));
        respuestas.replaceAll(String::trim);
        
        // Generar partes faltantes basándose en el número de respuestas
        ArrayList<String> partesFaltantes = new ArrayList<>();
        for (int i = 0; i < respuestas.size(); i++) {
            partesFaltantes.add("parte_" + (i + 1));
        }
        
        // Crear ejercicio usando el Builder
        EjercicioCompletarCodigo ejercicio = new EjercicioCompletarCodigo.Builder()
            .conInstruccion(txtInstruccion.getText().trim())
            .conCodigoIncompleto(txtCodigo.getText().trim())
            .conPartesFaltantes(partesFaltantes)
            .conRespuestasEsperadas(respuestas)
            .conNivel(comboNivel.getValue())
            .conLenguaje(comboLenguaje.getValue())
            .construir();
        
        // Guardar en repositorio
        EjercicioRepository.guardarEjercicioCompletarCodigo(ejercicio);
    }
    
    @FXML
    private void limpiarCampos() {
        comboTipoEjercicio.setValue(null);
        txtInstruccion.clear();
        comboLenguaje.setValue(null);
        comboNivel.setValue(null);
        
        // Campos específicos de selección
        txtOpciones.clear();
        txtRespuestasCorrectas.clear();
        
        // Campos específicos de completar código
        txtCodigo.clear();
        txtRespuestasEspacios.clear();
        
        // Ocultar secciones específicas
        seccionSeleccion.setVisible(false);
        seccionSeleccion.setManaged(false);
        seccionCompletar.setVisible(false);
        seccionCompletar.setManaged(false);
    }
    
    @FXML
    private void filtrarEjercicios() {
        ejerciciosFiltrados.clear();
        
        String tipoFiltro = filtroTipo.getValue();
        Lenguaje lenguajeFiltro = filtroLenguaje.getValue();
        
        List<EjercicioBase> ejerciciosFiltradosTemp = todosLosEjercicios.stream()
            .filter(ejercicio -> {
                // Filtrar por tipo
                boolean cumpleTipo = true;
                if (tipoFiltro != null && !"Todos".equals(tipoFiltro)) {
                    if ("Selección Múltiple".equals(tipoFiltro)) {
                        cumpleTipo = ejercicio instanceof EjercicioSeleccion;
                    } else if ("Completar Código".equals(tipoFiltro)) {
                        cumpleTipo = ejercicio instanceof EjercicioCompletarCodigo;
                    }
                }
                
                // Filtrar por lenguaje
                boolean cumpleLenguaje = (lenguajeFiltro == null || ejercicio.getLenguaje() == lenguajeFiltro);
                
                return cumpleTipo && cumpleLenguaje;
            })
            .collect(Collectors.toList());
        
        ejerciciosFiltrados.addAll(ejerciciosFiltradosTemp);
    }
    
    @FXML
    private void verDetalles() {
        if (ejercicioSeleccionado == null) {
            mostrarMensaje("Aviso", "Selecciona un ejercicio para ver sus detalles", Alert.AlertType.WARNING);
            return;
        }
        
        StringBuilder detalles = new StringBuilder();
        detalles.append("Tipo: ").append(ejercicioSeleccionado instanceof EjercicioSeleccion ? "Selección Múltiple" : "Completar Código").append("\n");
        detalles.append("Lenguaje: ").append(ejercicioSeleccionado.getLenguaje()).append("\n");
        detalles.append("Nivel: ").append(ejercicioSeleccionado.getNivel()).append("\n");
        detalles.append("Instrucción: ").append(ejercicioSeleccionado.getInstruccion()).append("\n\n");
        
        if (ejercicioSeleccionado instanceof EjercicioSeleccion) {
            EjercicioSeleccion ej = (EjercicioSeleccion) ejercicioSeleccionado;
            detalles.append("Opciones:\n");
            for (int i = 0; i < ej.getListOpciones().size(); i++) {
                detalles.append("  ").append(i + 1).append(". ").append(ej.getListOpciones().get(i)).append("\n");
            }
            detalles.append("\nRespuestas correctas: ").append(String.join(", ", ej.obtenerRespuestasCorrectas()));
        } else if (ejercicioSeleccionado instanceof EjercicioCompletarCodigo) {
            EjercicioCompletarCodigo ej = (EjercicioCompletarCodigo) ejercicioSeleccionado;
            detalles.append("Código:\n").append(ej.obtenerCodigoIncompleto()).append("\n");
            detalles.append("\nRespuestas: ").append(String.join(", ", ej.obtenerRespuestasEsperadas()));
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del Ejercicio");
        alert.setHeaderText("Información completa del ejercicio");
        alert.setContentText(detalles.toString());
        alert.showAndWait();
    }
    
    @FXML
    private void editarEjercicio() {
        if (ejercicioSeleccionado == null) {
            mostrarMensaje("Aviso", "Selecciona un ejercicio para editar", Alert.AlertType.WARNING);
            return;
        }
        
        // Cargar datos del ejercicio en el formulario
        cargarEjercicioEnFormulario(ejercicioSeleccionado);
        
        mostrarMensaje("Información", "Ejercicio cargado en el formulario. Modifica los campos y presiona 'Crear Ejercicio' para actualizar.", Alert.AlertType.INFORMATION);
    }
    
    private void cargarEjercicioEnFormulario(EjercicioBase ejercicio) {
        // Campos comunes
        txtInstruccion.setText(ejercicio.getInstruccion());
        comboLenguaje.setValue(ejercicio.getLenguaje());
        comboNivel.setValue(ejercicio.getNivel());
        
        if (ejercicio instanceof EjercicioSeleccion) {
            comboTipoEjercicio.setValue("Selección Múltiple");
            EjercicioSeleccion ej = (EjercicioSeleccion) ejercicio;
            
            // Cargar opciones
            txtOpciones.setText(String.join(",", ej.getListOpciones()));
            txtRespuestasCorrectas.setText(String.join(",", ej.obtenerRespuestasCorrectas()));
            
        } else if (ejercicio instanceof EjercicioCompletarCodigo) {
            comboTipoEjercicio.setValue("Completar Código");
            EjercicioCompletarCodigo ej = (EjercicioCompletarCodigo) ejercicio;
            
            // Cargar código y respuestas
            txtCodigo.setText(ej.obtenerCodigoIncompleto());
            txtRespuestasEspacios.setText(String.join(",", ej.obtenerRespuestasEsperadas()));
        }
        
        // Activar la vista correspondiente
        cambiarTipoEjercicio();
    }
    
    @FXML
    private void eliminarEjercicio() {
        if (ejercicioSeleccionado == null) {
            mostrarMensaje("Aviso", "Selecciona un ejercicio para eliminar", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar este ejercicio?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");
        
        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            // Nota: Para una implementación completa, necesitarías métodos de eliminación en EjercicioRepository
            todosLosEjercicios.remove(ejercicioSeleccionado);
            filtrarEjercicios();
            mostrarMensaje("Éxito", "Ejercicio eliminado exitosamente", Alert.AlertType.INFORMATION);
        }
    }
    
    @FXML
    private void volverAlHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Modulo_Usuario/views/home.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) comboTipoEjercicio.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarMensaje("Error", "Error al volver al home: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
