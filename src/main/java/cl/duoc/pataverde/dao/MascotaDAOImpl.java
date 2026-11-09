package cl.duoc.pataverde.dao;

import cl.duoc.pataverde.model.Gato;
import cl.duoc.pataverde.model.Perro;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MascotaDAOImpl implements MascotaDAO {

    private static final String ARCHIVO_PERROS = "perros.json";
    private static final String ARCHIVO_GATOS = "gatos.json";
    private final Gson gson;

    public MascotaDAOImpl() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public List<Perro> cargarPerros() {
        File file = new File(ARCHIVO_PERROS);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Perro>>() {}.getType();
            List<Perro> lista = gson.fromJson(reader, listType);
            return (lista != null) ? lista : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Gato> cargarGatos() {
        File file = new File(ARCHIVO_GATOS);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Gato>>() {}.getType();
            List<Gato> lista = gson.fromJson(reader, listType);
            return (lista != null) ? lista : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void guardarPerros(List<Perro> perros) {
        try (Writer writer = new FileWriter(ARCHIVO_PERROS)) {
            gson.toJson(perros, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void guardarGatos(List<Gato> gatos) {
        try (Writer writer = new FileWriter(ARCHIVO_GATOS)) {
            gson.toJson(gatos, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}