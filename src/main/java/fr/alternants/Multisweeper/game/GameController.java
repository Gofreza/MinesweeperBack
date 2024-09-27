package fr.alternants.Multisweeper.game;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;


    @MessageMapping("/play")
    @SendTo("/topic/game")
    public PlayResponse play(@Payload PlayRequest playRequest) {
        System.out.println("Play request: " + playRequest);
        return gameService.play(playRequest);
    }


    @MessageMapping("/flag")
    @SendTo("/topic/game")
    public PlayResponse flag(@Payload PlayRequest playRequest) {
        System.out.println("Flag request: " + playRequest);
        return gameService.flag(playRequest);
    }

    @MessageMapping("/grid")
    @SendTo("/topic/game")
    public PlayResponse getGrid(@Payload Integer roomId) {
        System.out.println("Get grid, roomId: " + roomId);
        List<PlayResponse.CellResponse> cellResponses = gameService.getGrid(roomId);
        return new PlayResponse(cellResponses, gameService.soloGames.get(roomId).isGameEnded(), gameService.soloGames.get(roomId).isGameWin(), roomId, "", "getGrid");
    }

    @MessageMapping("/delete")
    @SendTo("/topic/game")
    public String deleteRoom(@Payload Integer roomId) {
        System.out.println("Get grid, roomId: " + roomId);
        gameService.deleteRoom(roomId);
        return "Delete successful";
    }
}
