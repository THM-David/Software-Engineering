package de.thm.se.backend.controller;

import de.thm.se.backend.DataAcessLayer.SemesterzeitDAO;
import de.thm.se.backend.model.Semesterzeit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/semesterzeit")
public class SemesterZeitController {

    @Autowired
    private SemesterzeitDAO semesterzeitDAO;

    @GetMapping
    public List<Semesterzeit> getAllSemester() throws SQLException {
        return semesterzeitDAO.findAll();
    }

    @GetMapping("/{id}")
    public Semesterzeit getSemester(@PathVariable int id) throws SQLException {
        return semesterzeitDAO.findById(id).orElse(null);
    }

    @PostMapping
    public Semesterzeit createSemester(@RequestBody Semesterzeit semesterzeit) throws SQLException {
        int id = semesterzeitDAO.create(semesterzeit);
        semesterzeit.setSemesterzeitId(id);
        return semesterzeit;
    }

    @PutMapping("/{id}")
    public Semesterzeit updateSemester(@PathVariable int id, @RequestBody Semesterzeit semesterzeit) throws SQLException {
        semesterzeit.setSemesterzeitId(id);
        semesterzeitDAO.update(semesterzeit);
        return semesterzeit;
    }

    @DeleteMapping("/{id}")
    public void deleteSemester(@PathVariable int id) throws SQLException {
        semesterzeitDAO.delete(id);
    }
}