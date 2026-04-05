package edu.eci.dosw.DOSW_Library.persistence.nonrelational.document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
public class BookDocument {

    @Id
    private String id;
    private String titulo;
    private String autor;
    private String isbn;
    private List<String> categorias;
    private String tipoPublicacion;
    private LocalDate fechaPublicacion;
    private LocalDate fechaAgregado;
    private int totalStock;
    private int availableStock;
    private BookMetadataDocument metadata;
    private BookAvailabilityDocument availability;

    public BookDocument() {
        this.categorias = new ArrayList<>();
    }

    public BookDocument(String id, String titulo, String autor, String isbn, List<String> categorias,
                        String tipoPublicacion, LocalDate fechaPublicacion, LocalDate fechaAgregado,
                        int totalStock, int availableStock, BookMetadataDocument metadata,
                        BookAvailabilityDocument availability) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categorias = categorias;
        this.tipoPublicacion = tipoPublicacion;
        this.fechaPublicacion = fechaPublicacion;
        this.fechaAgregado = fechaAgregado;
        this.totalStock = totalStock;
        this.availableStock = availableStock;
        this.metadata = metadata;
        this.availability = availability;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public List<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    public String getTipoPublicacion() {
        return tipoPublicacion;
    }

    public void setTipoPublicacion(String tipoPublicacion) {
        this.tipoPublicacion = tipoPublicacion;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public LocalDate getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDate fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public BookMetadataDocument getMetadata() {
        return metadata;
    }

    public void setMetadata(BookMetadataDocument metadata) {
        this.metadata = metadata;
    }

    public BookAvailabilityDocument getAvailability() {
        return availability;
    }

    public void setAvailability(BookAvailabilityDocument availability) {
        this.availability = availability;
    }

    public static class BookMetadataDocument {

        private int paginas;
        private String idioma;
        private String empresa;

        public BookMetadataDocument() {
        }

        public BookMetadataDocument(int paginas, String idioma, String empresa) {
            this.paginas = paginas;
            this.idioma = idioma;
            this.empresa = empresa;
        }

        public int getPaginas() {
            return paginas;
        }

        public void setPaginas(int paginas) {
            this.paginas = paginas;
        }

        public String getIdioma() {
            return idioma;
        }

        public void setIdioma(String idioma) {
            this.idioma = idioma;
        }

        public String getEmpresa() {
            return empresa;
        }

        public void setEmpresa(String empresa) {
            this.empresa = empresa;
        }
    }

    public static class BookAvailabilityDocument {

        private String status;
        private int totalCopias;
        private int copiasDisponibles;
        private int copiasPrestadas;

        public BookAvailabilityDocument() {
        }

        public BookAvailabilityDocument(String status, int totalCopias, int copiasDisponibles, int copiasPrestadas) {
            this.status = status;
            this.totalCopias = totalCopias;
            this.copiasDisponibles = copiasDisponibles;
            this.copiasPrestadas = copiasPrestadas;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getTotalCopias() {
            return totalCopias;
        }

        public void setTotalCopias(int totalCopias) {
            this.totalCopias = totalCopias;
        }

        public int getCopiasDisponibles() {
            return copiasDisponibles;
        }

        public void setCopiasDisponibles(int copiasDisponibles) {
            this.copiasDisponibles = copiasDisponibles;
        }

        public int getCopiasPrestadas() {
            return copiasPrestadas;
        }

        public void setCopiasPrestadas(int copiasPrestadas) {
            this.copiasPrestadas = copiasPrestadas;
        }
    }
}

