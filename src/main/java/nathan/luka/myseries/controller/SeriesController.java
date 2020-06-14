package nathan.luka.myseries.controller;

import nathan.luka.myseries.dataprovider.DataProvider;
import nathan.luka.myseries.model.Review;
import nathan.luka.myseries.model.Serie;
import nathan.luka.myseries.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

@Controller
public class SeriesController {
    private final DataProvider model = DataProvider.getInstance();

    //load all series

    @GetMapping("/series")
    public String series(Model model, HttpSession httpSession) {
        if (!isLoggedIn(httpSession)) {
            return "redirect:/login";
        }
        model.addAttribute("series", this.model.getSeries());
        return "series";
    }

    @GetMapping("/index")
    public String homeView(Model model) {
        model.addAttribute("home_page", this.model.getSeries());
        return "index";
    }

    // TODO: 14/06/2020 series voor solo user
    @GetMapping("/series_solo")
    public String soloSeries(Model model, HttpSession httpSession){
        if (isLoggedIn(httpSession)){
            model.addAttribute("series",   model.addAttribute("series", this.model.getUserByUsername(String.valueOf(httpSession.getAttribute("username"))).getSerie()));
            return "series";
        }
        return "redirect:login";
    }

    @PostMapping("/series")
    public String addSerie(@ModelAttribute(value = "perie") Serie serie, HttpSession httpSession) {
        if (!isLoggedIn(httpSession)) {
            return "redirect:/login";
        }
        User user1 = model.getUserByUsername((String) httpSession.getAttribute("username"));
        if (user1 != null) {
            serie.setUser(user1);
            serie.setTitle(serie.getTitle());
            model.addSerie(serie);
            return "series";
        }
        return "series";
    }

    //get a specific serie

    @GetMapping("/serie/{id}")
    public String getSerie(@PathVariable("id") int id, Model model, HttpSession httpSession) {
        if (!isLoggedIn(httpSession)) {
            return "redirect:/login";
        }
        Serie serie = this.model.getSerieById(id);
        if (serie != null) {
            model.addAttribute("serie", serie);
            return "serie";
        }
        return null;
    }

    @PostMapping("/serie/delete/{id}")
    public RedirectView removeSerieTitle(@PathVariable("id") int id, HttpSession httpSession) {
        if (!isLoggedIn(httpSession)) {
            return new RedirectView("/login");
        }
        Serie serie = this.model.getSerieById(id);
        if (serie != null) {
            this.model.getSeries().remove(serie);
        }
        return new RedirectView("/series");
    }

    // TODO: 14/06/2020 niet werkend
    @PostMapping("/serie/update/{id}")
    public RedirectView setSerieTitle(@PathVariable("id") int id, @RequestBody String updated_name, HttpSession httpSession) {
        if (!isLoggedIn(httpSession)) {
            return new RedirectView("/login");
        }
        Serie serie = this.model.getSerieById(id);
        if (serie != null) {
            serie.setTitle(updated_name);
        }
        return new RedirectView("/series");
    }


    //add review

    @PostMapping("/series/{id}")
    public RedirectView addReviewToSerie(@PathVariable("id") int id,
                                         @RequestBody Review review,
                                         @RequestParam("user") String username) {
        if (model.hasUserWithUsername(username)) {
            Serie serie = model.getSerieById(id);
            if (serie != null) {
                review.setUser(model.getUserByUsername(username));
                serie.addReview(review);
                return new RedirectView("/serie");
            } else {
                return new RedirectView("/series");
            }
        } else {
            return new RedirectView("/series");
        }
    }

    private boolean isLoggedIn(HttpSession session) {
        return (session.getAttribute("username") != null);
    }


}
