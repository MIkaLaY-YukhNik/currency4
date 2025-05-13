package com.example.currency4.controller;

import com.example.currency4.entity.ConversionHistory;
import com.example.currency4.service.ConversionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversion-histories")
public class ConversionHistoryController {

    @Autowired
    private ConversionHistoryService conversionHistoryService;

    @GetMapping
    public List<ConversionHistory> getAllConversionHistories() {
        return conversionHistoryService.getAllConversionHistories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversionHistory> getConversionHistoryById(@PathVariable Long id) {
        return conversionHistoryService.getConversionHistoryById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ConversionHistory createConversionHistory(@RequestBody ConversionHistory conversionHistory) {
        return conversionHistoryService.createConversionHistory(conversionHistory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConversionHistory> updateConversionHistory(@PathVariable Long id, @RequestBody ConversionHistory conversionHistoryDetails) {
        try {
            ConversionHistory updatedHistory = conversionHistoryService.updateConversionHistory(id, conversionHistoryDetails);
            return ResponseEntity.ok(updatedHistory);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversionHistory(@PathVariable Long id) {
        try {
            conversionHistoryService.deleteConversionHistory(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}