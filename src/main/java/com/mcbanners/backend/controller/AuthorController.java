package com.mcbanners.backend.controller;

import com.mcbanners.backend.banner.param.author.AuthorParameter;
import com.mcbanners.backend.image.layout.AuthorLayout;
import com.mcbanners.backend.obj.generic.Author;
import com.mcbanners.backend.service.ServiceBackend;
import com.mcbanners.backend.service.api.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("author")
public class AuthorController {
    private final AuthorService authors;

    @Autowired
    public AuthorController(AuthorService authors) {
        this.authors = authors;
    }

    @GetMapping(value = "/spigot/{id}/isValid", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Boolean>> getIsValid(@PathVariable int id) {
        Author author = this.authors.getAuthor(id, ServiceBackend.SPIGET);
        return new ResponseEntity<>(Collections.singletonMap("valid", author != null), HttpStatus.OK);
    }

    @GetMapping(value = "/spigot/{id}/banner.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getBanner(@PathVariable int id, @RequestParam Map<String, String> raw) {
        Author author = this.authors.getAuthor(id, ServiceBackend.SPIGET);
        if (author == null) {
            return null;
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            BufferedImage banner = new AuthorLayout(author, AuthorParameter.parse(raw)).draw();
            ImageIO.write(banner, "png", bos);
            bos.flush();
            return new ResponseEntity<>(bos.toByteArray(), HttpStatus.OK);
        } catch (IOException ex) {
            return new ResponseEntity<>(new byte[]{}, HttpStatus.NO_CONTENT);
        }
    }
}
