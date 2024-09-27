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


    public NewGameResponse newGame(NewGameRequest newGameRequest) {
        NewGameResponse newGameResponse = new NewGameResponse();
        Integer roomId = rand.nextInt();
        if (newGameRequest.getIsMultiplayer()) {
            Multisweeper multisweeper = new Multisweeper(newGameRequest.getRows(), newGameRequest.getCols(), newGameRequest.getDifficulty());
            multiGames.put(roomId, multisweeper);
            newGameResponse.setBombNumber(multisweeper.getNbBombs());
        } else {
            soloGames.put(roomId, new Solosweeper(newGameRequest.getRows(), newGameRequest.getCols(), newGameRequest.getDifficulty()));
            newGameResponse.setBombNumber(soloGames.get(roomId).getNbBombs());
        }



        newGameResponse.setRoomId(roomId);
        newGameResponse.setUsername(newGameRequest.getUsername());
        System.out.println("New game created : " + newGameResponse + " Multiplayer: " + newGameRequest.getIsMultiplayer());
        return newGameResponse;

    }


    public PlayResponse play(PlayRequest playRequest) {
        PlayResponse playResponse = new PlayResponse();
        Solosweeper solosweeper;
        if (playRequest.getUsername() == null && soloGames.containsKey(playRequest.getRoomId())) {
            solosweeper = soloGames.get(playRequest.getRoomId());

        } else if (playRequest.getUsername() != null && multiGames.containsKey(playRequest.getRoomId()) && multiGames.get(playRequest.getRoomId()).getGames().containsKey(playRequest.getUsername())) {
            solosweeper = multiGames.get(playRequest.getRoomId()).getGames().get(playRequest.getUsername());
            playResponse.setUsername(playRequest.getUsername());
        } else return null;

        playResponse.setChangedCells(solosweeper.play(playRequest.getRow(), playRequest.getCol()));
        playResponse.setRoomId(playRequest.getRoomId());

        checkWin(solosweeper, playResponse);
        return playResponse;
    }

    public PlayResponse flag(PlayRequest playRequest) {
        PlayResponse playResponse = new PlayResponse();
        Solosweeper solosweeper;
        if (playRequest.getUsername() == null && soloGames.containsKey(playRequest.getRoomId())) {
            solosweeper = soloGames.get(playRequest.getRoomId());


        } else if (playRequest.getUsername() != null && multiGames.containsKey(playRequest.getRoomId()) && multiGames.get(playRequest.getRoomId()).getGames().containsKey(playRequest.getUsername())) {
            solosweeper = multiGames.get(playRequest.getRoomId()).getGames().get(playRequest.getUsername());
            playResponse.setUsername(playRequest.getUsername());
        } else return null;

        playResponse.setChangedCells(solosweeper.flag(playRequest.getRow(), playRequest.getCol()));
        playResponse.setRoomId(playRequest.getRoomId());

        checkWin(solosweeper, playResponse);
        return playResponse;
    }

    public List<PlayResponse.CellResponse> getGrid(Integer roomId) {
        if (soloGames.containsKey(roomId)) {
            Solosweeper solosweeper = soloGames.get(roomId);
            return solosweeper.getGrid();
        } else if (multiGames.containsKey(roomId)) {
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
