package fr.alternants.Multisweeper.game;

import fr.alternants.Multisweeper.game.core.Multisweeper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GameService {
    private final HashMap<Integer, Multisweeper> soloGames = new HashMap<>();

    private final Random rand = new Random();


    public Integer newGame(NewGameRequest newGameRequest) {
        Integer roomId;
        if (newGameRequest.getIsMultiplayer()) {
            return null;
        } else {
            roomId = newSoloGame(newGameRequest);
            System.out.println("New solo game created with roomId: " + roomId);
        }

        return roomId;
    }

    public Integer newSoloGame(NewGameRequest newGameRequest) {
        Integer roomId = rand.nextInt();
        soloGames.put(roomId, new Multisweeper(newGameRequest.getRows(), newGameRequest.getCols(), newGameRequest.getIsMultiplayer(), newGameRequest.getDifficulty()));
        return roomId;
    }

    public PlayResponse play(PlayRequest playRequest) {
        if (playRequest.getPlayerId() == null) {
            Multisweeper multisweeper = soloGames.get(playRequest.getRoomId());
            PlayResponse playResponse = new PlayResponse();
            playResponse.setChangedCells(multisweeper.play(playRequest.getRow(), playRequest.getCol()));
            multisweeper.checkGameWin();
            playResponse.setIsGameWin(multisweeper.isGameWin());
            playResponse.setIsGameEnded(multisweeper.isGameEnded());
            return playResponse;
        }
        return null;
    }

    public PlayResponse.CellResponse flag(PlayRequest playRequest) {
        if (playRequest.getPlayerId() == null) {
            Multisweeper multisweeper = soloGames.get(playRequest.getRoomId());
            return multisweeper.flag(playRequest.getRow(), playRequest.getCol());
        }
        return null;
    }

    public List<PlayResponse.CellResponse> getGrid(Integer roomId) {
        if (soloGames.containsKey(roomId)) {
            Multisweeper multisweeper = soloGames.get(roomId);
            return multisweeper.getVisibleGrid();
        }
        return null;
    }
}
