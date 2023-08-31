package ru.gb.gbthymeleaf.proxy.product.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import ru.gb.gbthymeleaf.dao.ProductDao;
import ru.gb.gbthymeleaf.entity.Product;
import ru.gb.gbthymeleaf.proxy.product.ProductProxyMapper;
import ru.gb.gbthymeleaf.proxy.product.Proxy;
import ru.gb.gbthymeleaf.proxy.product.entity.ProductProxy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductProxyService implements Proxy {
    private Map<Long, ProductProxy> productById;
    private final ProductDao productDao;
    private final ProductProxyMapper mapper;

    @Override
    public ProductProxy save(ProductProxy productProxy) {
        var savedProductProxy = saveProduct(productProxy);

        productById.put(savedProductProxy.getId(), savedProductProxy);
        return productById.get(savedProductProxy.getId());
    }

    private ProductProxy saveProduct(ProductProxy productProxy) {
        if (productProxy.getId() != null) {
            Optional<Product> productOptional = productDao.findById(productProxy.getId());
            if (productOptional.isPresent()) {
                Product productFromDB = productOptional.get();
                productFromDB.setTitle(productProxy.getTitle());
                productFromDB.setManufactureDate(productProxy.getManufactureDate());
                productFromDB.setStatus(productProxy.getStatus());
                productFromDB.setCost(productProxy.getCost());
                return mapper.toProxy(productDao.save(productFromDB));
            }
        }
        return mapper.toProxy(productDao.save(mapper.toModel(productProxy)));
    }

    @Override
    public ProductProxy findById(Long id) {
        return productById.get(id);
    }

    @Override
    public List<ProductProxy> findAll() {
        if (productById == null) {
            productById = productDao.findAll().stream()
                    .collect(Collectors.toMap(
                            Product::getId,
                            mapper::toProxy
                    ));
        }

        return productById.values().stream().toList();
    }

    @Override
    public void deleteById(Long id) {
        try {
            productDao.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            log.error("No such id in DB: " + e.getMessage());
        }

        productById.remove(id);
    }
}
