package fr.alternants.Multisweeper.game;

import fr.alternants.Multisweeper.game.core.Multisweeper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GameService {
    private HashMap<Integer, Multisweeper> soloGames = new HashMap<>();

    private Random rand = new Random();


    public ResponseEntity<Integer> newGame(NewGameRequest newGameRequest) {
        Integer roomId;
        if (newGameRequest.getIsMultiplayer()) {
            return null;
        } else {
            roomId = newSoloGame(newGameRequest);
            System.out.println("New solo game created with roomId: " + roomId);
        }

        return ResponseEntity.ok(roomId);
    }

    public Integer newSoloGame(NewGameRequest newGameRequest) {
        Integer roomId = rand.nextInt();
        soloGames.put(roomId, new Multisweeper(newGameRequest.getRows(), newGameRequest.getCols(), newGameRequest.getIsMultiplayer(), newGameRequest.getDifficulty()));
        return roomId;
    }

    public ResponseEntity<PlayResponse> play(PlayRequest playRequest) {
        if (playRequest.getPlayerId() == null) {
            Multisweeper multisweeper = soloGames.get(playRequest.getRoomId());
            PlayResponse playResponse = new PlayResponse();
            playResponse.setChangedCells(multisweeper.play(playRequest.getRow(), playRequest.getCol()));
            multisweeper.checkGameWin();
            playResponse.setIsGameWin(multisweeper.isGameWin());
            playResponse.setIsGameEnded(multisweeper.isGameEnded());
            return ResponseEntity.ok(playResponse);
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<Boolean> flag(PlayRequest playRequest) {
        if (playRequest.getPlayerId() != null) {
            Multisweeper multisweeper = soloGames.get(playRequest.getRoomId());
            return ResponseEntity.ok(multisweeper.flag(playRequest.getRow(), playRequest.getCol()));
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<List<PlayResponse.CellResponse>> getGrid(Long roomId) {
        if (soloGames.containsKey(roomId)) {
            Multisweeper multisweeper = soloGames.get(roomId);
            return ResponseEntity.ok(multisweeper.getVisibleGrid());
        }
        return ResponseEntity.badRequest().build();
    }
}
