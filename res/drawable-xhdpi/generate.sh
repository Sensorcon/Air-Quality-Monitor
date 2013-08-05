#!/bin/bash

for FILE in \
info.png
do
	convert $FILE -resize 35% ../drawable-ldpi/$FILE
	convert $FILE -resize 40% ../drawable-mdpi/$FILE
	convert $FILE -resize 70% ../drawable-hdpi/$FILE
	convert $FILE -resize 145% ../drawable-xxhdpi/$FILE
done
