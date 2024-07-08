package Model;

public class GuruMataPelajaranRelation {
    private String idGuru;
    private String namaGuru;
    private String idMataPelajaran;
    private String namaMataPelajaran;

    public GuruMataPelajaranRelation(String idGuru, String namaGuru, String idMataPelajaran, String namaMataPelajaran) {
        this.idGuru = idGuru;
        this.namaGuru = namaGuru;
        this.idMataPelajaran = idMataPelajaran;
        this.namaMataPelajaran = namaMataPelajaran;
    }

    public String getIdGuru() {
        return idGuru;
    }

    public void setIdGuru(String idGuru) {
        this.idGuru = idGuru;
    }

    public String getNamaGuru() {
        return namaGuru;
    }

    public void setNamaGuru(String namaGuru) {
        this.namaGuru = namaGuru;
    }

    public String getIdMataPelajaran() {
        return idMataPelajaran;
    }

    public void setIdMataPelajaran(String idMataPelajaran) {
        this.idMataPelajaran = idMataPelajaran;
    }

    public String getNamaMataPelajaran() {
        return namaMataPelajaran;
    }

    public void setNamaMataPelajaran(String namaMataPelajaran) {
        this.namaMataPelajaran = namaMataPelajaran;
    }
}
