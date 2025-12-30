package de.thm.se.backend.controller;

import de.thm.se.backend.DataAcessLayer.ZeitdatenDAO;
import de.thm.se.backend.model.Zeitdaten;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/semester")
public class ZeitdatenController {

    @Autowired
    private ZeitdatenDAO zeitdatenDAO;

    @GetMapping
    public List<Zeitdaten> getAllSemester() throws SQLException {
        return zeitdatenDAO.findAll();
    }

    @GetMapping("/{id}")
    public Zeitdaten getSemester(@PathVariable int id) throws SQLException {
        return zeitdatenDAO.findById(id).orElse(null);
    }

    @PostMapping
    public Zeitdaten createSemester(@RequestBody Zeitdaten zeit) throws SQLException {
        int id = zeitdatenDAO.create(zeit);
        zeit.setZeitId(id);
        return zeit;
    }

    @PutMapping("/{id}")
    public Zeitdaten updateSemester(@PathVariable int id, @RequestBody Zeitdaten zeit) throws SQLException {
        zeit.setZeitId(id);
        zeitdatenDAO.update(zeit);
        return zeit;
    }

    @DeleteMapping("/{id}")
    public void deleteSemester(@PathVariable int id) throws SQLException {
        zeitdatenDAO.delete(id);
    }
}