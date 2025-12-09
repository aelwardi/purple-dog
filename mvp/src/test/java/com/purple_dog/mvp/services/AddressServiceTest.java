package com.purple_dog.mvp.services;

import com.purple_dog.mvp.dao.AddressRepository;
import com.purple_dog.mvp.dao.PersonRepository;
import com.purple_dog.mvp.dto.AddressCreateDTO;
import com.purple_dog.mvp.dto.AddressResponseDTO;
import com.purple_dog.mvp.dto.AddressUpdateDTO;
import com.purple_dog.mvp.entities.Address;
import com.purple_dog.mvp.entities.Individual;
import com.purple_dog.mvp.entities.UserRole;
import com.purple_dog.mvp.exceptions.InvalidOperationException;
import com.purple_dog.mvp.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private AddressService addressService;

    private Individual person;
    private Address address;

    @BeforeEach
    void setUp() {
        person = Individual.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.INDIVIDUAL)
                .build();

        address = Address.builder()
                .id(1L)
                .person(person)
                .label("Domicile")
                .street("123 Rue de la République")
                .city("Paris")
                .postalCode("75001")
                .country("France")
                .isDefault(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateFirstAddress_ShouldBeDefaultAutomatically() {
        AddressCreateDTO dto = AddressCreateDTO.builder()
                .label("Domicile")
                .street("123 Rue de la République")
                .city("Paris")
                .postalCode("75001")
                .country("France")
                .build();

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(addressRepository.countByPersonId(1L)).thenReturn(0L);
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressResponseDTO result = addressService.createAddress(1L, dto);

        assertNotNull(result);
        assertTrue(result.getIsDefault());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void testCreateAddress_WithIsDefaultTrue_ShouldResetOthers() {
        AddressCreateDTO dto = AddressCreateDTO.builder()
                .label("Travail")
                .street("456 Avenue")
                .city("Paris")
                .postalCode("75002")
                .country("France")
                .isDefault(true)
                .build();

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(addressRepository.countByPersonId(1L)).thenReturn(1L);
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        addressService.createAddress(1L, dto);

        verify(addressRepository, times(1)).resetDefaultForPerson(1L);
    }

    @Test
    void testCreateAddress_MaxLimitReached_ShouldThrowException() {
        AddressCreateDTO dto = AddressCreateDTO.builder()
                .label("Test")
                .street("Test Street")
                .city("Paris")
                .postalCode("75001")
                .country("France")
                .build();

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(addressRepository.countByPersonId(1L)).thenReturn(10L);

        assertThrows(InvalidOperationException.class, () -> {
            addressService.createAddress(1L, dto);
        });
    }

    @Test
    void testGetUserAddresses_Success() {
        List<Address> addresses = Arrays.asList(address);
        when(personRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.findByPersonIdOrderByIsDefaultDescCreatedAtDesc(1L))
                .thenReturn(addresses);

        List<AddressResponseDTO> result = addressService.getUserAddresses(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Domicile", result.get(0).getLabel());
    }

    @Test
    void testGetAddress_Success() {
        when(addressRepository.findByIdAndPersonId(1L, 1L))
                .thenReturn(Optional.of(address));

        AddressResponseDTO result = addressService.getAddress(1L, 1L);

        assertNotNull(result);
        assertEquals("Domicile", result.getLabel());
        assertTrue(result.getIsDefault());
    }

    @Test
    void testGetAddress_NotFound() {
        when(addressRepository.findByIdAndPersonId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            addressService.getAddress(1L, 1L);
        });
    }

    @Test
    void testGetDefaultAddress_Success() {
        when(personRepository.existsById(1L)).thenReturn(true);
        when(addressRepository.findByPersonIdAndIsDefault(1L, true))
                .thenReturn(Optional.of(address));

        AddressResponseDTO result = addressService.getDefaultAddress(1L);

        assertNotNull(result);
        assertTrue(result.getIsDefault());
    }

    @Test
    void testUpdateAddress_Success() {
        AddressUpdateDTO dto = AddressUpdateDTO.builder()
                .label("Nouvelle adresse")
                .street("789 Boulevard")
                .build();

        when(addressRepository.findByIdAndPersonId(1L, 1L))
                .thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressResponseDTO result = addressService.updateAddress(1L, 1L, dto);

        assertNotNull(result);
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    void testSetDefaultAddress_Success() {
        address.setIsDefault(false);

        when(addressRepository.findByIdAndPersonId(1L, 1L))
                .thenReturn(Optional.of(address));
        when(addressRepository.save(any(Address.class))).thenReturn(address);

        AddressResponseDTO result = addressService.setDefaultAddress(1L, 1L);

        verify(addressRepository, times(1)).resetDefaultForPerson(1L);
        verify(addressRepository, times(1)).save(address);
    }

    @Test
    void testDeleteAddress_WasDefault_ShouldSetNewDefault() {
        Address address2 = Address.builder()
                .id(2L)
                .person(person)
                .label("Travail")
                .street("456 Avenue")
                .city("Paris")
                .postalCode("75002")
                .country("France")
                .isDefault(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(addressRepository.findByIdAndPersonId(1L, 1L))
                .thenReturn(Optional.of(address));
        when(addressRepository.findByPersonIdOrderByIsDefaultDescCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(address2));

        addressService.deleteAddress(1L, 1L);

        verify(addressRepository, times(1)).delete(address);
        verify(addressRepository, times(1)).save(address2);
    }

    @Test
    void testCountUserAddresses() {
        when(addressRepository.countByPersonId(1L)).thenReturn(3L);

        long count = addressService.countUserAddresses(1L);

        assertEquals(3L, count);
    }

    @Test
    void testHasAddresses_True() {
        when(addressRepository.countByPersonId(1L)).thenReturn(2L);

        boolean hasAddresses = addressService.hasAddresses(1L);

        assertTrue(hasAddresses);
    }

    @Test
    void testHasAddresses_False() {
        when(addressRepository.countByPersonId(1L)).thenReturn(0L);

        boolean hasAddresses = addressService.hasAddresses(1L);

        assertFalse(hasAddresses);
    }

    @Test
    void testFullAddressFormat_WithComplement() {
        address.setComplement("Appartement 4B");
        when(addressRepository.findByIdAndPersonId(1L, 1L))
                .thenReturn(Optional.of(address));

        AddressResponseDTO result = addressService.getAddress(1L, 1L);

        assertTrue(result.getFullAddress().contains("Appartement 4B"));
        assertTrue(result.getFullAddress().contains("75001"));
        assertTrue(result.getFullAddress().contains("Paris"));
    }
}

