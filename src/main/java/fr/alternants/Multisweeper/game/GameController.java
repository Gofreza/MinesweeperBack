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

    @MessageMapping("/new")
    @SendTo("/topic/game")
    public Integer newGame(@Payload NewGameRequest newGameRequest) {
        System.out.println("New game request: " + newGameRequest);
        return gameService.newGame(newGameRequest);
    }


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
        return new PlayResponse(cellResponses, false, false);    }

    @MessageMapping("/delete")
    @SendTo("/topic/game")
    public String deleteRoom(@Payload Integer roomId) {
        System.out.println("Get grid, roomId: " + roomId);
        gameService.deleteRoom(roomId);
        return "Delete successful";
    }
}
