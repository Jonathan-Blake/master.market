package com.stocktrader.market.repo;

import com.stocktrader.market.model.dao.TraderDao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraderRepo extends CrudRepository<TraderDao, String> {
}
