# Lab104 Braille Recognition

# Общее описание решения

Проект является цифровым решением в рамках конкурса World AI&DATA Challenge, соответствующиим задаче "[Braille text optical recognition](https://git.asi.ru/tasks/world-ai-and-data-challenge/braille-text-optical-recognition)"

**Задача** состоит в автоматическом распознавании текстов, написанных с помощью шрифта Брайля, и их перевода на кириллицу и латиницу. 
Продукт должен справляться с фотографиями и сканированными изображениями, полученными без использования профессионального оборудования.

**Решение**: разработка мобильного приложения для распознавания шрифта Брайля на изображении.

## Общее описание логики работы решения

![Диаграмма](diagram.jpg)
**Текстовое описание**:
- Разработано мобильное приложение, которые является основным UI и служит для получения и выгрузки изображения с мобильного устройства. 
 - Полученное изображение последовательно разделяется на строки и на символы, формируя последовательность изображеий символов (Буквы, символы, цифры, знаки препинания и пробелы).
 - Полученная последовательность изображений отправляется на распознавание нейросети, которая выдает текст.
 - Текст выводится на экран. 
## Требования к окружению для запуска продукта
Платформа: Andoid

Стэк технологий:
...
## Сценарий сборки и запуска проекта
Приведите пошаговую инструкцию по запуску вашего проекта.

Используйте разметку `code` для указания команд, выполняемых в консоли.

`var a = 1
 echo a
`

## Примеры использования
В этой части описания вы можете представить видео или иные способы демонстрации работы UI или API вашего решения.

## Используемые наборы данных
Проложите к проекту набор данных, необходимых для запуска проекта. Максимальный допустимый размер одного файла равен 300 MB
Если необходимо загрузить файлы большего размера, то их можно порезать на несколько частей, используя архиватор ZIP, размер каждой части файта не должен превышать 300 MB

Отдельный аспект - какие данные, кроме доступных в задаче, требуются дополнительно для того, чтобы решение работало или работотало эффективнее. Описание особенностей и порядка их использования в решении.

## Дополнительный инструментарий

Дополнительные инструменты, которые требуются для развёртывания решения.
