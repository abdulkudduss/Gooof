package com.example.gooo.init;

import com.example.gooo.domain.embeddable.Address;
import com.example.gooo.domain.entity.*;
import com.example.gooo.domain.enums.UserRole;
import com.example.gooo.domain.repository.CarrierRepository;
import com.example.gooo.domain.repository.ProductRepository;
import com.example.gooo.domain.repository.ShippingMethodRepository;
import com.example.gooo.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CarrierRepository carrierRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            initUsers();
        }
        
        if (productRepository.count() == 0) {
            initProducts();
        }

        if (carrierRepository.count() == 0) {
            initShipmentData();
        }
    }

    private void initProducts() {
        Product p1 = new Product();
        p1.setName("iPhone 15");
        p1.setSku("APPLE-IPHONE-15");
        p1.setCurrentPrice(new BigDecimal("999.00"));

        Product p2 = new Product();
        p2.setName("MacBook Pro");
        p2.setSku("APPLE-MBP-14");
        p2.setCurrentPrice(new BigDecimal("1999.00"));

        Product p3 = new Product();
        p3.setName("AirPods Pro");
        p3.setSku("APPLE-AIRPODS-PRO");
        p3.setCurrentPrice(new BigDecimal("249.00"));

        productRepository.saveAll(List.of(p1, p2, p3));
    }

    private void initShipmentData() {
        Carrier c1 = new Carrier();
        c1.setName("Global Express");
        c1.setActive(true);
        carrierRepository.save(c1);

        ShippingMethod m1 = new ShippingMethod();
        m1.setName("Economy");
        m1.setCarrier(c1);

        ShippingMethod m2 = new ShippingMethod();
        m2.setName("Priority");
        m2.setCarrier(c1);

        shippingMethodRepository.saveAll(List.of(m1, m2));

        Carrier c2 = new Carrier();
        c2.setName("Postal Service");
        c2.setActive(true);
        carrierRepository.save(c2);

        ShippingMethod m3 = new ShippingMethod();
        m3.setName("Standard");
        m3.setCarrier(c2);

        shippingMethodRepository.saveAll(List.of(m3));
    }

    private void initUsers() {
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword("admin123"); // В реальном приложении — хешировать!
        admin.setRole(UserRole.ADMIN);

        UserProfile adminProfile = new UserProfile();
        adminProfile.setUser(admin);
        adminProfile.setFirstName("Admin");
        adminProfile.setLastName("System");
        Address adminAddress = new Address(
                "Unknown",
                "Unknown",
                "0",
                "000000",
                "KG"
        );

        adminProfile.setDefaultAddress(adminAddress);

        admin.setProfile(adminProfile);

        User customer = new User();
        customer.setEmail("customer@example.com");
        customer.setPassword("user123");
        customer.setRole(UserRole.CUSTOMER);

        UserProfile customerProfile = new UserProfile();
        customerProfile.setUser(customer);
        customerProfile.setFirstName("Ivan");
        customerProfile.setLastName("Ivanov");
        customerProfile.setPhone("+79991112233");
        
        Address address = new Address("Moscow", "Lenina", "10", "101000", "RU");
        customerProfile.setDefaultAddress(address);
        customer.setProfile(customerProfile);

        userRepository.saveAll(List.of(admin, customer));
    }
}
