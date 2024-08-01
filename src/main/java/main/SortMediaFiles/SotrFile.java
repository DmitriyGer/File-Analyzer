package main.SortMediaFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Исполняющая функция для сортировки медиафайлов
 */
public class SotrFile {

    private static final String[] MONTHS = {
        "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
        "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    };

    public static void sortFiles(List<File> files, boolean checkByYear, boolean checkByMonth, boolean checkByDay, File targetDir, String choice) {
        for (File file : files) {
            try {
                long lastModified = file.lastModified();
                Date lastModifiedDate = new Date(lastModified);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(lastModifiedDate);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                
                File currentTargetDir = targetDir;
                if (checkByYear) {
                    currentTargetDir = new File(currentTargetDir, String.valueOf(year));
                    createDirectoryIfNotExists(currentTargetDir);
                }
                if (checkByMonth) {
                    currentTargetDir = new File(currentTargetDir, MONTHS[month]);
                    createDirectoryIfNotExists(currentTargetDir);
                }
                if (checkByDay) {
                    currentTargetDir = new File(currentTargetDir, String.format("%02d", day));
                    createDirectoryIfNotExists(currentTargetDir);
                }
                
                Path targetPath = currentTargetDir.toPath().resolve(file.getName());

                if (choice.equals("Отсортировать в текущей папке")) {
                    Files.move(file.toPath(), targetPath);
                } else if (choice.equals("Переместить отсортированные файлы в")) {
                    Files.move(file.toPath(), targetPath);
                } else if (choice.equals("Копировать отсортированные файлы в")) {
                    Files.copy(file.toPath(), targetPath);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void createDirectoryIfNotExists(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}