#!/usr/bin/ruby

countries = ['AUS', 'BEL', 'CAN', 'CHN', 'FRA', 'GBR', 'GER', 'GRC', 'GRL', 
             'IRL', 'NLD', 'NOR', 'SJM', 'SWE', 'TTO', 'TZA', 'USA', 'ZMB']

200.times { 
  country = countries[rand(countries.length)]
  score = 1 + rand(1000)
  puts "#{country},#{score}"
}
