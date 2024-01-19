package ua.goit.urlshortener.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.goit.urlshortener.url.UrlService;

@Controller
@RequestMapping("V2/url")
@RequiredArgsConstructor
public class UrlWebController {
    private final UrlService urlService;

    @GetMapping()
    public ModelAndView getIndexPage(){
        return new ModelAndView("index");
    }

    @GetMapping("/all")
    public ModelAndView getAllLinks(){
        ModelAndView result = new ModelAndView();
        // result.addObject("notes", noteService.listAll());
        return result;
    }

    @GetMapping("/register")
    public ModelAndView registerUser(){
        ModelAndView result = new ModelAndView();
       // result.addObject("notes", noteService.listAll());
        return result;
    }

    @GetMapping("/login")
    public ModelAndView loginUser(){
        ModelAndView result = new ModelAndView();
        // result.addObject("notes", noteService.listAll());
        return result;
    }
}
