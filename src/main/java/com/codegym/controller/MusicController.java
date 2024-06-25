package com.codegym.controller;

import com.codegym.model.entity.Music;
import com.codegym.model.dto.MusicForm;
import com.codegym.service.IMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/musics")
@PropertySource("classpath:upload_file.properties")
public class MusicController {

    @Value("${upload.path}")
    private String upload;

    @Autowired
    private IMusicService musicService;

    @GetMapping("")
    public String index(Model model) {
        List<Music> musicList = musicService.findAll();
        model.addAttribute("musics", musicList);
        return "/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("musicForm", new MusicForm());
        return "/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("musicForm") @Validated MusicForm musicForm,
                       BindingResult bindingResult, RedirectAttributes attributes,
                       Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "/create";
        } else {
            MultipartFile file = musicForm.getSong();
            String song = file.getOriginalFilename();
            File uploadDir = new File(upload);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            FileCopyUtils.copy(file.getBytes(), new File(upload + File.separator + song));

            Music music = new Music();
            music.setName(musicForm.getName());
            music.setArtist(musicForm.getArtist());
            music.setMusicGenre(musicForm.getMusicGenre());
            music.setLinkSong(song);

            try {
                musicService.save(music);
                attributes.addFlashAttribute("success", "Music saved successfully!");
                return "redirect:/musics";
            } catch (RuntimeException e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "/create";
            }
        }
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable int id, Model model) {
        Music music = musicService.findById(id);
        if (music != null) {
            MusicForm musicForm = new MusicForm(music.getId(), music.getName(), music.getArtist(), music.getMusicGenre(), null);
            model.addAttribute("musicForm", musicForm);
        }
        return "/update";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable int id, @ModelAttribute("musicForm") @Validated MusicForm musicForm,
                         BindingResult bindingResult, RedirectAttributes attributes,
                         Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "/update";
        } else {
            MultipartFile file = musicForm.getSong();
            String song = file.getOriginalFilename();
            File uploadDir = new File(upload);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            if (song != null && !song.isEmpty()) {
                FileCopyUtils.copy(file.getBytes(), new File(upload + File.separator + song));
            } else {
                song = musicService.findById(id).getLinkSong();
            }

            Music music = new Music();
            music.setId(id);
            music.setName(musicForm.getName());
            music.setArtist(musicForm.getArtist());
            music.setMusicGenre(musicForm.getMusicGenre());
            music.setLinkSong(song);

            try {
                musicService.save(music);
                attributes.addFlashAttribute("success", "Music updated successfully!");
                return "redirect:/musics";
            } catch (RuntimeException e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "/update";
            }
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable int id, Model model) {
        model.addAttribute("music", musicService.findById(id));
        return "/delete";
    }

    @PostMapping("/delete")
    public String delete(Music music, RedirectAttributes redirect) {
        musicService.remove(music.getId());
        redirect.addAttribute("success", "Removed music successfully!");
        return "redirect:/musics";
    }

    @GetMapping("/{id}/view")
    public String view(@PathVariable int id, Model model) {
        model.addAttribute("music", musicService.findById(id));
        return "/view";
    }

    @GetMapping("/search")
    public String search(@RequestParam String name, Model model){
        model.addAttribute("musics",musicService.findByName(name));
        return "/index";
    }
}
