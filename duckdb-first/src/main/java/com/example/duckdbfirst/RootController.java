package com.example.duckdbfirst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RootController {

  @Autowired
  SpeedFragmentDao speedFragmentDao;

  @RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<SpeedFragment> list(@RequestParam int day, @RequestParam long trafficId) {
    return speedFragmentDao.find(day, trafficId);
	}
}

