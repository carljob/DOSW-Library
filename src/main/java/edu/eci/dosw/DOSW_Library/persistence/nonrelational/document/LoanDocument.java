package edu.eci.dosw.DOSW_Library.persistence.nonrelational.document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "loans")
public class LoanDocument {

    @Id
    private String id;
    private String libroId;
    private String usuarioId;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;
    private LocalDate fechaAgregado;
    private List<LoanHistoryEntryDocument> historial;

    public LoanDocument() {
        this.historial = new ArrayList<>();
    }

    public LoanDocument(String id, String libroId, String usuarioId, LocalDate fechaPrestamo,
                        LocalDate fechaDevolucion, boolean devuelto, LocalDate fechaAgregado,
                        List<LoanHistoryEntryDocument> historial) {
        this.id = id;
        this.libroId = libroId;
        this.usuarioId = usuarioId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
        this.fechaAgregado = fechaAgregado;
        this.historial = historial;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLibroId() {
        return libroId;
    }

    public void setLibroId(String libroId) {
        this.libroId = libroId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

    public LocalDate getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDate fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }

    public List<LoanHistoryEntryDocument> getHistorial() {
        return historial;
    }

    public void setHistorial(List<LoanHistoryEntryDocument> historial) {
        this.historial = historial;
    }

    public static class LoanHistoryEntryDocument {

        private String status;
        private LocalDate fechaEjecucion;

        public LoanHistoryEntryDocument() {
        }

        public LoanHistoryEntryDocument(String status, LocalDate fechaEjecucion) {
            this.status = status;
            this.fechaEjecucion = fechaEjecucion;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDate getFechaEjecucion() {
            return fechaEjecucion;
        }

        public void setFechaEjecucion(LocalDate fechaEjecucion) {
            this.fechaEjecucion = fechaEjecucion;
        }
    }
}

