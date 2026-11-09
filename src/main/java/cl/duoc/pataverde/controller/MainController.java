package cl.duoc.pataverde.controller;

import cl.duoc.pataverde.model.Gato;
import cl.duoc.pataverde.model.Mascota;
import cl.duoc.pataverde.model.Perro;
import cl.duoc.pataverde.repositorio.MascotaRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.text.NumberFormat;
import java.util.Locale;

public class MainController {

    // Componentes del Formulario
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtEdad;
    @FXML private TextField txtPeso;

    // Componentes Perro
    @FXML private TitledPane panePerro;
    @FXML private TextField txtRaza;
    @FXML private CheckBox chkVacunado;
    @FXML private CheckBox chkAdopcion;

    // Componentes Gato
    @FXML private TitledPane paneGato;
    @FXML private CheckBox chkExterior;

    @FXML private Button btnRegistrar;
    @FXML private Button btnEliminar;
    @FXML private Label lblMensaje;

    // Tabla
    @FXML private TableView<Mascota> tblMascotas;
    @FXML private TableColumn<Mascota, String> colTipo;
    @FXML private TableColumn<Mascota, String> colNombre;
    @FXML private TableColumn<Mascota, Integer> colEdad;
    @FXML private TableColumn<Mascota, Double> colPeso;
    @FXML private TableColumn<Mascota, String> colCosto;

    private MascotaRepository repository;
    private ObservableList<Mascota> listaObservable;
    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));

    @FXML
    public void initialize() {
        repository = new MascotaRepository();
        cmbTipo.setItems(FXCollections.observableArrayList("Perro", "Gato"));

        configurarTabla();
        configurarEventos();
        deshabilitarFormularioEspecifico();

        cargarDatosTabla();
    }

    private void configurarTabla() {
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipo()));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));

        // Formato para mostrar costo ($15.000)
        colCosto.setCellValueFactory(cellData ->
                new SimpleStringProperty(formatoMoneda.format(cellData.getValue().calcularCostoConsulta()))
        );
    }

    private void configurarEventos() {
        cmbTipo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Perro".equals(newVal)) {
                panePerro.setDisable(false);
                paneGato.setDisable(true);
                limpiarCamposGato();
            } else if ("Gato".equals(newVal)) {
                panePerro.setDisable(true);
                paneGato.setDisable(false);
                limpiarCamposPerro();
            } else {
                deshabilitarFormularioEspecifico();
            }
        });
    }

    private void deshabilitarFormularioEspecifico() {
        panePerro.setDisable(true);
        paneGato.setDisable(true);
    }

    private void cargarDatosTabla() {
        listaObservable = FXCollections.observableArrayList(repository.obtenerTodas());
        tblMascotas.setItems(listaObservable);
    }

    @FXML
    private void handleRegistrar() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            mostrarError("Tipo: debe seleccionar un tipo de mascota.");
            return;
        }

        String nombre = txtNombre.getText();
        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarError("Nombre: no puede estar vacío ni contener solo espacios.");
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(txtEdad.getText().trim());
            if (edad < 0 || edad > 30) {
                mostrarError("Edad: debe ser un número entero entre 0 y 30.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("Edad: debe ser un número entero entre 0 y 30.");
            return;
        }

        double peso;
        try {
            peso = Double.parseDouble(txtPeso.getText().trim());
            if (peso <= 0) {
                mostrarError("Peso (kg): debe ser un número mayor que cero.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("Peso (kg): debe ser un número mayor que cero.");
            return;
        }

        Mascota nuevaMascota = null;

        if ("Perro".equals(tipo)) {
            String raza = txtRaza.getText();
            if (raza == null || raza.trim().isEmpty()) {
                mostrarError("Raza: no puede estar vacía ni contener solo espacios.");
                return;
            }
            boolean vacunado = chkVacunado.isSelected();
            boolean adopcion = chkAdopcion.isSelected();
            nuevaMascota = new Perro(nombre, edad, peso, raza, vacunado, adopcion);
        } else if ("Gato".equals(tipo)) {
            boolean exterior = chkExterior.isSelected();
            nuevaMascota = new Gato(nombre, edad, peso, exterior);
        }

        if (nuevaMascota != null) {
            repository.agregarMascota(nuevaMascota);
            cargarDatosTabla();
            mostrarExito(nombre + " (" + tipo + ") registrado correctamente.");
            limpiarFormulario();
        }
    }

    @FXML
    private void handleEliminar() {
        Mascota seleccionada = tblMascotas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarError("Debe seleccionar una mascota de la tabla para eliminarla.");
            return;
        }

        String nombre = seleccionada.getNombre();
        String tipo = seleccionada.getTipo();

        repository.eliminarMascota(seleccionada);
        cargarDatosTabla();
        mostrarExito(nombre + " (" + tipo + ") eliminado correctamente.");
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setTextFill(Color.RED);
        lblMensaje.setText(mensaje);
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setTextFill(Color.GREEN);
        lblMensaje.setText(mensaje);
    }

    private void limpiarFormulario() {
        cmbTipo.getSelectionModel().clearSelection();
        txtNombre.clear();
        txtEdad.clear();
        txtPeso.clear();
        limpiarCamposPerro();
        limpiarCamposGato();
        deshabilitarFormularioEspecifico();
    }

    private void limpiarCamposPerro() {
        txtRaza.clear();
        chkVacunado.setSelected(false);
        chkAdopcion.setSelected(false);
    }

    private void limpiarCamposGato() {
        chkExterior.setSelected(false);
    }
}