package org.example.demo2;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Data {
    private int id;
    private String aprasymas;
    private String dalykas;
    private String pavadinimas;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate terminas;

    public Data() {}
    public Data(int id, String aprasymas, String dalykas, String pavadinimas, LocalDate terminas) {
        this.id = id;
        this.aprasymas = aprasymas;
        this.dalykas = dalykas;
        this.pavadinimas = pavadinimas;
        this.terminas = terminas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAprasymas() {
        return aprasymas;
    }

    public void setAprasymas(String aprasymas) {
        this.aprasymas = aprasymas;
    }

    public String getDalykas() {
        return dalykas;
    }

    public void setDalykas(String dalykas) {
        this.dalykas = dalykas;
    }

    public String getPavadinimas() {
        return pavadinimas;
    }

    public void setPavadinimas(String pavadinimas) {
        this.pavadinimas = pavadinimas;
    }

    public LocalDate getTerminas() {
        return terminas;
    }

    public void setTerminas(LocalDate terminas) {
        this.terminas = terminas;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id='" + id + '\'' +
                ", aprasymas='" + aprasymas + '\'' +
                ", dalykas='" + dalykas + '\'' +
                ", pavadinimas='" + pavadinimas + '\'' +
                ", terminas='" + terminas + '\'' +
                '}';
    }
}
