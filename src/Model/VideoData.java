package Model;

public class VideoData {
    private String id;
    private String judul;
    private String description;
    private String className;
    private String mataPelajaran;
    private String videoFileLink;

    public VideoData(String id, String judul, String description, String className, String mataPelajaran, String videoFileLink) {
        this.id = id;
        this.judul = judul;
        this.description = description;
        this.className = className;
        this.mataPelajaran = mataPelajaran;
        this.videoFileLink = videoFileLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMataPelajaran() {
        return mataPelajaran;
    }

    public void setMataPelajaran(String mataPelajaran) {
        this.mataPelajaran = mataPelajaran;
    }

    public String getVideoFileLink() {
        return videoFileLink;
    }

    public void setVideoFileLink(String videoFileLink) {
        this.videoFileLink = videoFileLink;
    }

    @Override
    public String toString() {
        return id + "," + judul + "," + description + "," + className + "," + mataPelajaran + "," + videoFileLink;
    }
}
