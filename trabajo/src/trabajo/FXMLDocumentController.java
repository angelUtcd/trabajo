package trabajo;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
public class FXMLDocumentController implements Initializable {
    EntityManagerFactory emf=Persistence.createEntityManagerFactory("trabajoPU");
    EntityManager em =emf.createEntityManager();
    private final ListChangeListener<Alumnos> selectortablaPersona = (ListChangeListener.Change<? extends Alumnos> c) -> {
        seleccionardate();
    };
    private final ObservableList<Alumnos> lista = FXCollections.observableArrayList();
    public Alumnos persona = new Alumnos();
    private int posicionAlumnosenTabla;
    @FXML
    private Label label;
    @FXML private TableView<Alumnos> tablapersona;
    @FXML private TableColumn<Alumnos, String> nombreCL;
    @FXML private TableColumn<Alumnos, String> apellidoCL;
    @FXML private TableColumn<Alumnos, Integer> ciCL;
    @FXML private TableColumn<Alumnos, String> telefonoCL;
    @FXML private TableColumn<Alumnos, String> ciudadCL;
    @FXML private TextField nombreTF;
    @FXML private TextField apellidoTF;
    @FXML private TextField ciTF;
    @FXML private TextField telefonoTF;
    @FXML private TextField ciudadTF;
    @FXML private Button aniadirBT;
    @FXML private Button modificarBT;
    @FXML private Button eliminarBT;
    @FXML private Button nuevoBT;
    @FXML
    private void aniadir(){
        em.getTransaction().begin();
        Alumnos persona = new Alumnos();
        persona.setNombre(nombreTF.getText());
        persona.setApellido(apellidoTF.getText());
        persona.setCi(ciTF.getText());
        persona.setTelefono(telefonoTF.getText());
        persona.setCiudad(ciudadTF.getText());
        em.persist(persona);
        em.getTransaction().commit();
        Platform.runLater(() ->{
            this.iniciardatos();
        });
    }
    public Alumnos gettablapersonaSeleccionada(){
        if (tablapersona != null) {
            List<Alumnos> tabla =tablapersona.getSelectionModel().getSelectedItems();
            if (tabla.size() == 1) {
                final Alumnos competicionSeleccionada= tabla.get(0);
                return  competicionSeleccionada;
            }
        }
        return null;
    }
    @FXML
    private void modificar(ActionEvent event) {
        em.getTransaction().begin();
        Alumnos p = persona;
        p.setNombre(nombreTF.getText());
        p.setApellido(apellidoTF.getText());
        p.setTelefono(telefonoTF.getText());
        p.setCiudad(ciudadTF.getText());
        p.setCi(ciTF.getText());
        em.merge(p);
        em.getTransaction().commit();
        Platform.runLater(() ->{
            this.iniciardatos();
        });
    }
    @FXML
    private void eliminar(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("ELIMINAR ALUMNOS");
        alert.setHeaderText("ALERTA, ALERTA, ALERTA");
        alert.setContentText("Estás seguro de eliminar el registro?");
        try {
             Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
            em.getTransaction().begin();
            Alumnos p = tablapersona.getSelectionModel().getSelectedItem();
            System.out.println("Se ha seleccionado: " + p.getNombre());
            em.remove(p);
            em.getTransaction().commit();
            Platform.runLater(() -> {
            this.iniciardatos();
            });
        } else {
            System.out.println("NO");
        }
        } catch (Exception e) {
            System.out.println("ERROR"+ e);
        }
    }
    @FXML
    private void nuevo(){
        nombreTF.setText("");
        apellidoTF.setText("");
        ciTF.setText("");
        telefonoTF.setText("");
        ciudadTF.setText("");
        modificarBT.setDisable(true);
        eliminarBT.setDisable(true);
        aniadirBT.setDisable(false);
    }
    public void inicializartablapersona(){
        tablapersona.setItems(lista);
        nombreCL.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidoCL.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        ciCL.setCellValueFactory(new PropertyValueFactory<>("ci"));
        telefonoCL.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        ciudadCL.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.iniciardatos();
        Platform.runLater(() -> {
        nombreTF.requestFocus();
        });
       this.inicializartablapersona();
        modificarBT.setDisable(true);
    }    
    public void iniciardatos(){
        lista.clear();
        TypedQuery<Alumnos> consulta = em.createQuery("SELECT a FROM Alumnos a", Alumnos.class);
        lista.addAll(consulta.getResultList());
    }
    public void modificarlista(){
        Alumnos persona = tablapersona.getSelectionModel().getSelectedItem();
        System.out.println("Nombre: " + persona.getNombre());
        nombreTF.setText(persona.getNombre());
        modificarBT.setDisable(false);
        eliminarBT.setDisable(false);
    }
    @FXML
    private  void seleccionardate(){
         persona = gettablapersonaSeleccionada();
         posicionAlumnosenTabla = lista.indexOf(persona);
         if (persona != null) {
            nombreTF.setText(persona.getNombre());
            apellidoTF.setText(persona.getApellido());
            ciTF.setText(persona.getCi());
            telefonoTF.setText(persona.getTelefono());
            ciudadTF.setText(persona.getCiudad());
            modificarBT.setDisable(false);
            eliminarBT.setDisable(false);
            aniadirBT.setDisable(true);
        } 
    }
}
