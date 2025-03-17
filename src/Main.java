import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    /*Добрый день Сергей
    * Я не до конца понял насчет меню, нужно ли его делать, по этому сделал, но так на скорую руку, не сильно обдумывал как оно должно выглядеть
    * Еще я не увидел в задании с самого начал подсказку по реализации через 3 HashMap,
    * по этому сделал иначе c одним ArrayList в Task и вложенным SubTask в EpicTask(У них второй свой ArrayList У каждого Epic)
    * Почему выбрал ArrayList а не HashMap. Т.к. у меня собираются в двух классах списки, ключи для HashMap или дополнительное поле ID в классе Task. Посчитал не имеет значение.
    * Не до конца понял какие методы должны возвращать Объекты. По этому добавил return только в поиск. Но без проблем можно в любой метод добавить return */
    public static TaskManager taskManager;
    public static Scanner scanner;

    public static void main(String[] args) {
            taskManager = new TaskManager();
            scanner = new Scanner(System.in);
            while (true){

            switch (printMenu()){
                case "1":
                    formToAddTask();
                break;
                case "2":
                    taskManager.printTasks();
                    break;
                case "3":
                    editTask();
                    break;
                case "4" :
                    System.out.println("Ведите ID целое число");
                    System.out.println(taskManager.findByID(scanner.nextInt()));
                    break;
                case "5" :
                    System.out.println("Ведите ID целое число");
                    taskManager.removeByID(scanner.nextInt());
                    break;
                case "6":
                    taskManager.removeAllTasks();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Такой задачи еще нет, попробуйте из списка");
            }
        }
    }

    private static void editTask() {
        System.out.println("Ведите ID целое число");
        int id = scanner.nextInt();
        boolean isFinishSwitch = true;
        while (isFinishSwitch){
            System.out.println("Что вы хотите изменить в задачах");
            System.out.println("1. Статус задачи");
            System.out.println("2. Название и описание");
            System.out.println("3. Отмена, ничего не хочу меня. Выход");

            switch (scanner.next()){

                case "1":
                    System.out.println("Введите новый статус: IN_PROGRESS, DONE, NEW");
                        switch (scanner.next()){
                            case "NEW":
                                taskManager.editTask(id,TaskProgress.NEW);
                                isFinishSwitch = false;
                                break;
                            case "IN_PROGRESS":
                                taskManager.editTask(id,TaskProgress.IN_PROGRESS);
                                isFinishSwitch = false;
                                break;
                            case "DONE":
                                taskManager.editTask(id,TaskProgress.DONE);
                                isFinishSwitch = false;
                                break;
                            default:
                                System.out.println("Нужно ввести один из статусов NEW, IN_PROGRESS, DONE");
                                break;
                        }
                break;

                case "2":
                    String[] newForm= new String[2];
                    System.out.println("Введите новое название: ");
                    newForm[0] = scanner.next();
                    System.out.println("Введите новое описание: ");
                    newForm[1] = scanner.next();
                    taskManager.editTask(id, newForm);
                    isFinishSwitch = false;
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Такой команды нет");
                    break;
            }
        }

    }

    private static void formToAddTask() {
        String[] form = new String[2];
        System.out.println("Введите название задачи: ");
        form[0] = scanner.next();
        System.out.println("Введите описание задачи: ");
        form[1] = scanner.next();
        System.out.println("Хотите добавить подзадачи? [y/n]");
        switch (scanner.next()){
            case "y":
                String[] formToEpicTask = new String[2];
                ArrayList<String[]> formToSubTask = new ArrayList<>();
                boolean isAllTaskHave = true;
                while (isAllTaskHave) {
                    System.out.println("Введите название подзадачи");
                    formToEpicTask[0] = scanner.next();
                    System.out.println("Введите описание подзадачи");
                    formToEpicTask[1] = scanner.next();
                    System.out.println("Будет еще подзадача? [y/n]");
                    formToSubTask.add(formToEpicTask);
                    switch (scanner.next()){
                        case "y":
                            break;
                        case "n":
                            isAllTaskHave = false;
                            break;
                        default:
                            System.out.println("Нужно ввести y - да или n - нет");
                            break;
                    }
                }
                taskManager.addTask(form,formToSubTask);
                break;
            case "n":
                taskManager.addTask(form);
                break;
            default:
                System.out.println("Нужно ввести y - да или n - нет");
                break;
        }


    }

    private static String printMenu() {
        System.out.println("Выберете, что нужно сделать?");
        System.out.println("1. Добавить задачу");
        System.out.println("2. Распечатать задачи");
        System.out.println("3. Изменить задачу");
        System.out.println("4. Поиск по ID");
        System.out.println("5. Удаление по ID");
        System.out.println("6. Удаление всех задач");
        System.out.println("0. Выход");
        return scanner.next();
    }
}
