package com.purple_dog.mvp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admins")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Admin extends Person {

    private Boolean superAdmin = false;

    @Column(columnDefinition = "TEXT")
    private String permissions;

    // Relations
    @OneToMany(mappedBy = "assignedAdmin", cascade = CascadeType.ALL)
    private List<SupportTicket> assignedTickets = new ArrayList<>();
}

