package app;

import app.common.collectionClasses.*;
import app.common.dataStructures.Pair;

public class CollectionInputControlller {
    public static StudyGroup checkCollectionInput(String studyGroupKey,
                                                                String studyGroupName,
                                                                String textStudyGroupX,
                                                                String textStudyGroupY,
                                                                String textStudyGroupStudentsCount,
                                                                String textShouldBeExpelled,
                                                                String textFormOfEducation,
                                                                String textSemester,
                                                                String personName,
                                                                String textColor,
                                                                String textPersonLocationX,
                                                                String textPersonLocationY,
                                                                String textPersonLocationZ,
                                                                String personPassportID,
                                                                String textCountry) throws Exception {
        Long key;
        Double x;
        Integer y;
        Integer studentsCount;
        Integer shouldBeExpelled;

        try {
            key = Long.parseLong(studyGroupKey);
        } catch (Exception e) {
            throw new Exception("Ключ - целое число");
        }

        try {
            x = Double.parseDouble(textStudyGroupX);
        } catch (Exception e) {
            throw new Exception("x-координата группы - число с плавающей точкой");
        }

        Coordinates coordinates;
        if (!textStudyGroupY.isEmpty()) {
            try {
                y = Integer.parseInt(textStudyGroupY);
                coordinates = new Coordinates(x, y);
            } catch (Exception e) {
                throw new Exception("y-координата группы - целое число");
            }
        } else {
            coordinates = new Coordinates(x);
        }

        StudyGroup studyGroup = new StudyGroup(studyGroupName, coordinates);

        if (!textStudyGroupStudentsCount.isEmpty()) {
            try {
                studentsCount = Integer.parseInt(textStudyGroupStudentsCount);
                studyGroup.setStudentsCount(studentsCount);
            } catch (Exception e) {
                throw new Exception("Количество учеников - целое число");
            }
        }

        if (!textShouldBeExpelled.isEmpty()) {
            try {
                shouldBeExpelled = Integer.parseInt(textShouldBeExpelled);
                studyGroup.setShouldBeExpelled(shouldBeExpelled);
            } catch (Exception e) {
               throw new Exception("Количество учеников, подлежащих исключению - натуральное число");
            }
        }

        FormOfEducation f = null;
        if (!textFormOfEducation.isEmpty()) {
            switch (textFormOfEducation) {
                case "distance_education" -> f = FormOfEducation.DISTANCE_EDUCATION;
                case "full_time_education" -> f = FormOfEducation.FULL_TIME_EDUCATION;
                case "evening_classes" -> f = FormOfEducation.EVENING_CLASSES;
            }
            studyGroup.setFormOfEducation(f);
        }

        Semester s = null;
        if (!textSemester.isEmpty()) {
            int semester = Integer.parseInt(textSemester);
            switch (semester) {
                case 3 -> s = Semester.THIRD;
                case 4 -> s = Semester.FOURTH;
                case 8 -> s = Semester.EIGHTH;
            }
            studyGroup.setSemester(s);
        }

        Double personX;
        Float personY;
        Integer personZ;

        if (!personName.isEmpty()) {
            Color personHairColor = null;
            switch (textColor) {
                case "red" -> personHairColor = Color.RED;
                case "yellow" -> personHairColor = Color.YELLOW;
                case "orange" -> personHairColor = Color.ORANGE;
            }

            Location location;
            try {
                personX = Double.parseDouble(textPersonLocationX);
            } catch (Exception e) {
                throw new Exception("x-координата человека - число с плавающей точкой");
            }

            try {
                personY = Float.parseFloat(textPersonLocationY);
            } catch (Exception e) {
                throw new Exception("y-координата человека - число с плавающей точкой");

            }
            if (!textPersonLocationZ.isEmpty()) {
                try {
                    personZ = Integer.parseInt(textPersonLocationZ);
                    location = new Location(personX, personY, personZ);
                } catch (Exception e) {
                    throw new Exception("z-координата человека - целое число");
                }
            } else {
                location = new Location(personX, personY);
            }

            Person person = new Person(personName, personHairColor, location);

            Country country = null;
            if (!textCountry.isEmpty()) {
                textCountry = textCountry.toLowerCase();
                switch (textCountry) {
                    case "russia" -> country = Country.RUSSIA;
                    case "france" -> country = Country.FRANCE;
                    case "india" -> country = Country.INDIA;
                    case "vatican" -> country = Country.VATICAN;
                    case "north_korea" -> country = Country.NORTH_KOREA;
                }
                person.setNationality(country);
            }

            if (!personPassportID.isEmpty()) {
                person.setPassportID(personPassportID);
            }

            studyGroup.setGroupAdmin(person);
        }

        return studyGroup;
    }

}
