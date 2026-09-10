# Lebron James User Guide

Lebron James is a desktop chatbot for keeping track of things you have to do.
You talk to it by typing, so if you can type fast you can get your task list
sorted faster than with a mouse-driven app.

> Screenshot to be added.

## Quick start

1. Make sure you have **Java 25** installed.
2. Download `lebronjames.jar` and put it in an empty folder.
3. Open a command window in that folder and run `java -jar "lebronjames.jar"`.
4. Type a command into the box at the bottom and press Enter.

Your tasks are saved to `data/lebronjames.txt` next to the JAR, and reloaded the
next time you start the app. There is no save command; every change is written
out straight away.

## Adding a todo

A todo is something with no date attached.

Format: `todo <description>`

Example: `todo read book`

```
Got it. I've added this task:
  [T][ ] read book 🏀
Now you have 1 tasks in the list.
```

## Adding a deadline

A deadline is something due by a particular date, optionally at a time.

Format: `deadline <description> /by <date>`

Example: `deadline return book /by 2019-06-06`

```
Got it. I've added this task:
  [D][ ] return book (by: Jun 06 2019) 🏀
Now you have 2 tasks in the list.
```

## Adding an event

An event runs from one date to another.

Format: `event <description> /from <start> /to <end>`

Example: `event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600`

```
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 06 2019, 2:00pm to: Aug 06 2019, 4:00pm) 🏀
Now you have 3 tasks in the list.
```

## Date formats

Anywhere a date is accepted you can write it as `yyyy-MM-dd` or `dd/MM/yyyy`,
optionally followed by a 24-hour time:

| You type | It means |
| --- | --- |
| `2019-12-02` | 2 December 2019, no particular time |
| `2/12/2019` | the same day, written the other way round |
| `2019-12-02 1800` | 2 December 2019 at 6:00pm |

Dates that do not exist are refused rather than quietly shifted, so
`2019-02-30` is reported as an error instead of becoming 28 February.

## Duplicate tasks are refused

If you try to add a task you already have, Lebron James turns it down and tells
you which task number it clashes with, rather than letting a second copy pile
up unnoticed.

Example: `todo read book` when that todo is already task 1

```
Oops! You already have that task, as number 1:
  [T][ ] read book
If you really need it twice, give one of them a different description.
```

Two tasks count as the same when **all** of these hold:

* they are the same kind of task (a todo is never a duplicate of a deadline,
  even with identical wording);
* their descriptions match, **ignoring capitalisation**, so `read book` and
  `Read Book` are the same task;
* their dates match, where the task kind has any.

That last rule is what lets a repeating chore stay on the list. These are two
different tasks, and both are accepted:

```
deadline submit report /by 2019-12-02
deadline submit report /by 2019-12-09
```

Whether a task has been ticked off makes no difference. Marking `read book` as
done does not make room for a second `read book`.

Two things worth knowing:

* Duplicates already sitting in your save file from before this check existed
  are **left alone**. They are still loaded and listed, because silently
  deleting tasks you had saved would be worse than showing them. Delete the
  ones you do not want with `delete`.
* Only the description is compared, character for character apart from case.
  `read book` and `read  book` (two spaces) are treated as different tasks.

## Listing tasks

Format: `list`

```
Here are the tasks in your list:
1.[T][ ] read book 🏀
2.[D][ ] return book (by: Jun 06 2019) 🏀
```

The numbers shown here are the ones to use with `mark`, `unmark` and `delete`.

## Marking a task as done

Format: `mark <task number>` and `unmark <task number>`

Example: `mark 1`

```
Nice one bro! This task is done:
  [X] read book 🏀
```

## Deleting a task

Format: `delete <task number>`

Example: `delete 1`

```
Noted. I've removed this task:
  [T][ ] read book 🏀
Now you have 1 tasks in the list.
```

## Seeing what is on for a day

Shows the deadlines and events falling on one date. Todos never appear here,
since they carry no date. A multi-day event shows up on every day it spans.

Format: `on <date>`

Example: `on 2019-12-02`

```
Here is what you have on Dec 02 2019:
1.[D][ ] submit report (by: Dec 02 2019) 🏀
```

## Searching by keyword

Finds tasks whose description contains the text you give. The search ignores
capitalisation and matches anywhere in the description, so `book` also finds
`bookshop`.

Format: `find <keyword>`

Example: `find book`

```
Here are the matching tasks in your list:
1.[T][ ] read book 🏀
```

## Exiting

Format: `bye`

```
Goodbye, I love basketball btw! 🏀
```

## Command summary

| Action | Format |
| --- | --- |
| Add todo | `todo <description>` |
| Add deadline | `deadline <description> /by <date>` |
| Add event | `event <description> /from <start> /to <end>` |
| List | `list` |
| Mark / unmark | `mark <number>`, `unmark <number>` |
| Delete | `delete <number>` |
| Day view | `on <date>` |
| Search | `find <keyword>` |
| Exit | `bye` |
