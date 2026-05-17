package com.skaichatbackend.contact;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    // POST /api/contacts/request
    @PostMapping("/request")
    public ResponseEntity<ContactResponse> sendRequest(
            @RequestBody Map<String, Long> body,
            Authentication authentication
    ) {
        Long senderId = (Long) authentication.getPrincipal();
        Long receiverId = body.get("receiverId");
        ContactResponse response = contactService.sendRequest(senderId, receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT /api/contacts/accept/:id
    @PutMapping("/accept/{id}")
    public ResponseEntity<ContactResponse> acceptRequest(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        ContactResponse response = contactService.acceptRequest(id, userId);
        return ResponseEntity.ok(response);
    }

    // DELETE /api/contacts/decline/:id
    @DeleteMapping("/decline/{id}")
    public ResponseEntity<Void> declineRequest(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        contactService.declineRequest(id, userId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/contacts/:id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeContact(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        contactService.removeContact(id, userId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/contacts
    @GetMapping
    public ResponseEntity<List<ContactResponse>> getMyContacts(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        List<ContactResponse> contacts = contactService.getMyContacts(userId);
        return ResponseEntity.ok(contacts);
    }

    // GET /api/contacts/requests
    @GetMapping("/requests")
    public ResponseEntity<List<ContactResponse>> getPendingRequests(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        List<ContactResponse> requests = contactService.getPendingRequests(userId);
        return ResponseEntity.ok(requests);
    }
}
