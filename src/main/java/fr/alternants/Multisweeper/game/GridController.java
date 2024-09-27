package fr.alternants.Multisweeper.game;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class GridController {
    private final GameService gameService;

    @GetMapping()
    public ResponseEntity<PlayResponse> getGrid(@RequestBody Integer roomId) {
        System.out.println("Get grid, roomId: " + roomId);
        List<PlayResponse.CellResponse> cellResponses = gameService.getGrid(roomId);
        return ResponseEntity.ok(new PlayResponse(cellResponses, false, false, roomId, ""));
    }
}
