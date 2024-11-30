package com.example.RestAPI;

import com.example.Model.Contact;
import com.example.Repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
class ContactApiController {
    private final ContactRepository repo;
    ContactApiController(ContactRepository repo) {
        this.repo = repo;
    }
    @GetMapping("/contactAPI")
    Iterable<Contact> olvasMind() {
        return repo.findAll();
    }
    @GetMapping("/contactAPI/{id}")
    Contact olvasEgy(@PathVariable int id) {
        return repo.findById(id)
                .orElseThrow(() -> new ContactNotFoundException(id));
    }

    @PostMapping("/contactAPI")
    Contact contactPost(@RequestBody Contact newContact) {
        return repo.save(newContact);
    }
    @PutMapping("/contactAPI/{id}")
    Contact contactPut(@RequestBody Contact contactData, @PathVariable int id) {
        return repo.findById(id)
                .map(a -> {
                    a.setName(contactData.getName());
                    a.setEmail(contactData.getEmail());
                    a.setMessage(contactData.getMessage());
                    a.setCreated_at(contactData.getCreated_at());
                    return repo.save(a);
                })
                .orElseGet(() -> {
                    contactData.setId(id);
                    return repo.save(contactData);
                });
    }
    @DeleteMapping("/contactAPI/{id}")
    void contactDelete(@PathVariable int id) {
        repo.deleteById(id);
    }
    static class ContactNotFoundException extends RuntimeException {
        ContactNotFoundException(int id) {
            super("A megadott id-vel nem létezik üzenet: " + id);
        }
    }

}
