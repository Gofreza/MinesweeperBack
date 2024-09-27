package fr.alternants.Multisweeper.game;

import fr.alternants.Multisweeper.game.core.Multisweeper;
import fr.alternants.Multisweeper.game.core.Solosweeper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final HashMap<Integer, Solosweeper> soloGames = new HashMap<>();

    private final HashMap<Integer, Multisweeper> multiGames = new HashMap<>();

    private final Random rand = new Random();

    private void checkWin(Solosweeper solosweeper, PlayResponse playResponse) {
        solosweeper.checkGameWin();
        playResponse.setIsGameWin(solosweeper.isGameWin());
        playResponse.setIsGameEnded(solosweeper.isGameEnded());

    }



    public Integer newGame(NewGameRequest newGameRequest) {
        Integer roomId;
        if (newGameRequest.getIsMultiplayer()) {
            return null;
        } else {
            roomId = rand.nextInt();
            if(newGameRequest.getIsMultiplayer()) multiGames.put(roomId, new Multisweeper(newGameRequest.getRows(), newGameRequest.getCols(), newGameRequest.getDifficulty()));
            else soloGames.put(roomId, new Solosweeper(newGameRequest.getRows(), newGameRequest.getCols(), newGameRequest.getDifficulty()));

            System.out.println("New game created with roomId: " + roomId + " Multiplayer: " + newGameRequest.getIsMultiplayer());
            return roomId;
        }
    }


    public PlayResponse play(PlayRequest playRequest) {
        PlayResponse playResponse = new PlayResponse();
        Solosweeper solosweeper;
        if (playRequest.getPlayerId() == null && soloGames.containsKey(playRequest.getRoomId())) {
            solosweeper = soloGames.get(playRequest.getRoomId());


        }
        else if (playRequest.getPlayerId() != null && multiGames.containsKey(playRequest.getRoomId()) && multiGames.get(playRequest.getRoomId()).getGames().containsKey(playRequest.getPlayerId())) {
            solosweeper = multiGames.get(playRequest.getRoomId()).getGames().get(playRequest.getPlayerId());
        }
        else return null;

        playResponse.setChangedCells(solosweeper.play(playRequest.getRow(), playRequest.getCol()));

        checkWin(solosweeper, playResponse);
        return playResponse;
    }

    public PlayResponse flag(PlayRequest playRequest) {
        PlayResponse playResponse = new PlayResponse();
        Solosweeper solosweeper;
        if (playRequest.getPlayerId() == null && soloGames.containsKey(playRequest.getRoomId())) {
            solosweeper = soloGames.get(playRequest.getRoomId());


        }
        else if (playRequest.getPlayerId() != null && multiGames.containsKey(playRequest.getRoomId()) && multiGames.get(playRequest.getRoomId()).getGames().containsKey(playRequest.getPlayerId())) {
            solosweeper = multiGames.get(playRequest.getRoomId()).getGames().get(playRequest.getPlayerId());
        }
        else return null;

        playResponse.setChangedCells(solosweeper.flag(playRequest.getRow(), playRequest.getCol()));

        checkWin(solosweeper, playResponse);
        return playResponse;
    }

    public List<PlayResponse.CellResponse> getGrid(Integer roomId) {
        if (soloGames.containsKey(roomId)) {
            Solosweeper solosweeper = soloGames.get(roomId);
            return solosweeper.getGrid();
        }
        else if (multiGames.containsKey(roomId)) {
            Multisweeper multisweeper = multiGames.get(roomId);
            return null;
        }
        return null;
    }

    public void deleteRoom(Integer roomId) {
        if (multiGames.containsKey(roomId)) multiGames.remove(roomId);
        else soloGames.remove(roomId);
    }
}
