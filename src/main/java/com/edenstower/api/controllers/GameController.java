package com.edenstower.api.controllers;

import com.edenstower.api.entities.Game;
import com.edenstower.api.entities.GameID;
import com.edenstower.api.entities.User;
import com.edenstower.api.repositories.GameRepository;
import com.edenstower.api.repositories.UserRepository;
import com.edenstower.api.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public List<Game> getAllGames(){return gameRepository.findAll();}

    @GetMapping("/game")
    public ResponseEntity getGame(@RequestParam String username, @RequestParam String slot){
        Game.SaveSlot saveSlot = Game.SaveSlot.valueOf(slot);
        if(!gameRepository.existsById(new GameID(username, saveSlot))){
            return ResponseEntity.notFound().build();
        }
        Optional<Game> game = gameRepository.findById(new GameID(username, saveSlot));
        return  ResponseEntity.ok().body(game);
    }

    @PostMapping("/game")
    public ResponseEntity postGame(@RequestParam String username, @RequestParam String difficulty, @RequestParam boolean fullScreen, @RequestParam boolean autoSave,
                                   @RequestParam int gammaLvl, @RequestParam boolean sfxEnabled, @RequestParam int sfxLvl, @RequestParam boolean musicEnabled,
                                   @RequestParam int musicLvl, @RequestParam int strength, @RequestParam int vitality, @RequestParam int defense, @RequestParam int speed,
                                   @RequestParam int luck, @RequestParam long totalKills, @RequestParam long totalDeaths, @RequestParam long gameTimeInSeconds,
                                   @RequestParam String saveData, @RequestParam String saveSlotstr){

        User user = userRepository.findByUsername(username);
        Game.SaveSlot saveSlot = Game.SaveSlot.valueOf(saveSlotstr);
        if(!gameRepository.existsById(new GameID(username, saveSlot))){
            int cantSaves = user.getGames().size();
            if(cantSaves < 4){
                Game game = new Game(
                        new GameID(username, saveSlot),
                        user,
                        saveData,
                        new Date(),
                        new Date(),
                        Game.Difficulty.valueOf(difficulty),
                        gameTimeInSeconds,
                        fullScreen,
                        autoSave,
                        gammaLvl,
                        sfxEnabled,
                        sfxLvl,
                        musicEnabled,
                        musicLvl,
                        strength,
                        vitality,
                        defense,
                        speed,
                        luck,
                        totalKills,totalDeaths
                );
                gameRepository.save(game);
                //user.getGames().add(game);
                return  ResponseEntity.ok().body(game);
            }
        }
        return ResponseEntity.notFound().build();

    }

    @PutMapping("/game")
    public ResponseEntity updateGame(@RequestParam String username, @RequestParam String difficulty, @RequestParam boolean fullScreen, @RequestParam boolean autoSave,
                                     @RequestParam int gammaLvl, @RequestParam boolean sfxEnabled, @RequestParam int sfxLvl, @RequestParam boolean musicEnabled,
                                     @RequestParam int musicLvl, @RequestParam int strength, @RequestParam int vitality, @RequestParam int defense, @RequestParam int speed,
                                     @RequestParam int luck, @RequestParam long totalKills, @RequestParam long totalDeaths, @RequestParam long gameTimeInSeconds,
                                     @RequestParam String saveData, @RequestParam String saveSlotstr){

        Game.SaveSlot saveSlot = Game.SaveSlot.valueOf(saveSlotstr);
        GameID gameID = new GameID(username, saveSlot);
        Optional<Game> oldGame = gameRepository.findById(gameID);

        if(oldGame.isPresent()){
            User user = userRepository.findByUsername(username);
            Game newGame = new Game(
                    gameID,
                    user,
                    saveData,
                    oldGame.get().getCreatedAt(),
                    new Date(),
                    Game.Difficulty.valueOf(difficulty),
                    gameTimeInSeconds,
                    fullScreen,
                    autoSave,
                    gammaLvl,
                    sfxEnabled,
                    sfxLvl,
                    musicEnabled,
                    musicLvl,
                    strength,
                    vitality,
                    defense,
                    speed,
                    luck,
                    totalKills,
                    totalDeaths
            );
            gameRepository.save(newGame);
            return  ResponseEntity.ok().body(newGame);
        }
        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/game")
    public Map<String, String> deleteGame(@RequestParam String username, @RequestParam String saveSlotstr){
        Game.SaveSlot saveSlot = Game.SaveSlot.valueOf(saveSlotstr);
        Map<String, String> response = new HashMap<>();
        Optional<Game> game = gameRepository.findById(new GameID(username, saveSlot));
        if(game.isEmpty()){
            response.put("deleted", "false");
            response.put("message", "Game not found");
        }else{
            gameRepository.delete(game.get());
            response.put("deleted", "true");
            response.put("message", "Game has been deleted");
        }
        return response;
    }

}
