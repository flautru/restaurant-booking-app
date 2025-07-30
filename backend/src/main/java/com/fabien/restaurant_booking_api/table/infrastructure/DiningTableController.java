package com.fabien.restaurant_booking_api.table.infrastructure;

import com.fabien.restaurant_booking_api.table.application.DiningTableMapper;
import com.fabien.restaurant_booking_api.table.application.DiningTableRequest;
import com.fabien.restaurant_booking_api.table.application.DiningTableResponse;
import com.fabien.restaurant_booking_api.table.application.DiningTableService;
import com.fabien.restaurant_booking_api.table.domain.DiningTable;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin("http://localhost:4200")
@RequiredArgsConstructor
public class DiningTableController {

  private final DiningTableService diningTableService;

  @GetMapping
  public ResponseEntity<List<DiningTableResponse>> findAll() {
    List<DiningTableResponse> tableResponses = diningTableService.findAll()
        .stream()
        .map(DiningTableMapper::toResponse)
        .toList();
    return ResponseEntity.ok(tableResponses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<DiningTableResponse> findById(@PathVariable Long id) {
    DiningTable table = diningTableService.findById(id);
    return ResponseEntity.ok(DiningTableMapper.toResponse(table));
  }

  @PostMapping
  public ResponseEntity<DiningTableResponse> create(
      @Valid @RequestBody DiningTableRequest request) {
    DiningTable table = DiningTableMapper.toEntity(request);

    DiningTableResponse response = DiningTableMapper.toResponse(diningTableService.create(table));
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(response.id())
        .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<DiningTableResponse> update(@PathVariable Long id,
      @Valid @RequestBody DiningTableRequest request) {
    DiningTable updatedTable = diningTableService.update(id, DiningTableMapper.toEntity(request));
    return ResponseEntity.ok(DiningTableMapper.toResponse(updatedTable));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    diningTableService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
