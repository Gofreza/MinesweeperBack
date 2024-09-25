package fr.alternants.Multisweeper.game;

import fr.alternants.Multisweeper.game.core.Multisweeper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GameService {
    private HashMap<Long, Multisweeper> soloGames = new HashMap<>();

    private Random rand = new Random();


    public ResponseEntity<Long> newGame(NewGameRequest newGameRequest) {
        Long roomId;
        if(newGameRequest.getIsMultiplayer()) {
            return null;
        }
        else{
            roomId = newSoloGame(newGameRequest);
        }

        return ResponseEntity.ok(roomId);
    }

    public Long newSoloGame(NewGameRequest newGameRequest) {
        Long roomId = rand.nextLong();
        soloGames.put(roomId, new Multisweeper(newGameRequest.getRows(), newGameRequest.getCols(), newGameRequest.getIsMultiplayer(), newGameRequest.getDifficulty()));
        return roomId;
    }

    public ResponseEntity<PlayResponse> play(PlayRequest playRequest) {
         if(playRequest.getPlayerId() != null){
             Multisweeper multisweeper = soloGames.get(playRequest.getRoomId());
             PlayResponse playResponse = new PlayResponse();
             playResponse.setChangedCells(multisweeper.play(playRequest.getRow(), playRequest.getCol()));
             multisweeper.checkGameWin();
             playResponse.setIsGameWin(multisweeper.isGameWin());
             playResponse.setIsGameEnded(multisweeper.isGameEnded());
         }
        return ResponseEntity.badRequest().build();
    }
}
