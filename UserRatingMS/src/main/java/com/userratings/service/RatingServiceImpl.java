package com.userratings.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.userratings.dao.BookRatingDAO;
import com.userratings.dao.UserRatingDAO;
import com.userratings.entity.BookRating;
import com.userratings.entity.UserRating;

@Service
public class RatingServiceImpl implements RatingService{
	
	@Autowired
	private BookRatingDAO bookRatingDAO;
	
	@Autowired
	private UserRatingDAO userRatingDAO;
	
	@Override
	public void addUserRating(UserRating userRating) {
		//1. Add the User Rating
		userRatingDAO.save(userRating); //BookId
		
		//2.Calculate the Avg rating for BookId
		int bookId = userRating.getBookId();
		List<UserRating> ratingList = userRatingDAO.getUserRatingByBookId(bookId);
		
		double sumRating = 0.0;
		for(UserRating ur: ratingList) {
			sumRating = sumRating+ur.getRating();
		}
		double avgRating = sumRating/ratingList.size();
		
		
		//3.Update BookRating in UserRatingMS(Local)
		BookRating bookRating = bookRatingDAO.findById(bookId).get();
		bookRating.setAvgRating(avgRating);
		bookRatingDAO.save(bookRating);
		
		//4. Update BookRating in BookSearchMS
		//Invoking BookRating-MS
		RestTemplate bookSearchRest = new RestTemplate();
		String endpoint = "http://localhost:8000/updateBookRating";
		bookSearchRest.put(endpoint, bookSearchRest);
	}

	@Override
	public List<UserRating> getUserRatingByUserId(String userId) {
		return userRatingDAO.getUserRatingByUserId(userId);
	}

	@Override
	public void updateBookRating(BookRating bookRating) {
		bookRatingDAO.save(bookRating);
	}

	@Override
	public BookRating getBookRatingByBookId(Integer bookId) {
		BookRating bookRating = bookRatingDAO.getBookRatingByBookId(bookId);
		return bookRating;
	}
	

}
