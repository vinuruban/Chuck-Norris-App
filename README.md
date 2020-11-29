# Chuck-Norris-App
This Android app is developed using Kotlin. It displays jokes fetched from the “The Internet Chuck Norris Database” API. 

# How the exercise was approached
- Agile methodology was followed to build this app.
- Prior to development, the work was broken down into smaller tasks and arranged them in order of priority.
- Read through the documentation and selected the URLs necessary for this exercise.

# Key factors of this application:
- A list of six random jokes can be seen. Clicking on the reload button will repopulate six different jokes. Explicit joke have already been filtered from the list.
- A specific joke can be found by entering the joke ID in the text field and clicking the search button.
- The special characters in the jokes are escaped in the list.
- Data validation is considered:
  - Submitting without providing any value in the text field will display the following error message - "Enter a valid joke ID".
  - The following will be returned if a non-existence joke ID is entered - "No joke found."
  - If the joke ID provides an explicit joke, this won't be shown on screen and the following will be returned instead - "Explicit jokes cannot be seen, please try another joke ID.".
- When there isn't any internet connection, the app will alert this when attempting to find jokes.
- The keyboard will minimise when the buttons are clicked - this improves the user experience.

# Further Improvements
- Saving jokes to favourites may be useful. This can be done by long-pressing a joke which will store the joke in the SQLite database. Fragments can be implemented to view the favourite jokes. There should be an option to delete the jokes from favourites.
- Recycler View is an enhanced version of List View, hence it will improve the app.
- Implementing Architecture Design will improve the user experience.
- Unit tests will solidify the app and find potential bugs. For this app, http status code can be checked when attempting to fetch the jokes from the API. Implementing an option to save favourite jokes will mean more tests should be written, i.e. checking jokes are stored in the database, they are deleted properly, and no duplicates jokes are saved. 
