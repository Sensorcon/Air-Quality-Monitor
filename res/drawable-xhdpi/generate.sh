#!/bin/bash

for FILE in \
face_bad.png \
face_good.png \
face_moderate.png \
face_unknown.png \
face_unknown_1.png \
face_unknown_2.png \
gas_view_bad.png \
gas_view_good.png \
gas_view_moderate.png
do
	convert $FILE -resize 35% ../drawable-ldpi/$FILE
	convert $FILE -resize 40% ../drawable-mdpi/$FILE
	convert $FILE -resize 70% ../drawable-hdpi/$FILE
	convert $FILE -resize 145% ../drawable-xxhdpi/$FILE
done
