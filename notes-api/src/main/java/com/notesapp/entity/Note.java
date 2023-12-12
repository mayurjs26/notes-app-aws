package com.notesapp.entity;


public record Note (
	String noteId,
	String text,
	String userId,
	String userName,
	long timeStamp,
	long expires
) {}
