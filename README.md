# CS360 Weight Tracker

## Briefly summarize the requirements and goals of the app you developed. What user needs was this app designed to address?

This app was created to allow users to track their daily weight and allow them to compare their weight to their set goal weight. Users that wanted to lose, gain, or just generally track their weight would be the main userbase for this application.

## What screens and features were necessary to support user needs and produce a user-centered UI for the app? How did your UI designs keep users in mind? Why were your designs successful?

My application takes advantage of four main screens. The login screen is designed in such a way to where a user can create their account and set their login weight easily while also logging in with the same username and password entry boxes. The weight entry screen doubles up as the database viewing screen where users can see and enter their daily weights. Users can also delete entries or modify the dates of the entries as well. The goal screen allows a user to see their current weight compared to their goal weight, and if the user decideds to, they can change their goal weight as well. Finally, the settings screen allows the user to enable SMS messages sent to their phone to let the user know that they're approaching or have surpassed their goal weight.

## How did you approach the process of coding your app? What techniques or strategies did you use? How could those techniques or strategies be applied in the future?

I wanted my design to be modular, therefore I created it in such a way that, if I decide to, I can easily add fragments into my main activity. This allows for easy scalability and maintainability of the app as any extra functionality will be relegated to individual fragments. I prefer to use this modularity approach going forward as it allows for the easiest way to add onto and maintain applications within reason.

## How did you test to ensure your code was functional? Why is this process important, and what did it reveal?

I used manual testing to ensure functionality. Since a lot of functionality was UI focused, I had to manually test to make sure the UI was functional. Unit tests for the backend database should have been used, but since I was pressed for time on this project, I was unable to implement these tests. Going forward, I will plan for unit tests where possible.

## Consider the full app design and development process from initial planning to finalization. Where did you have to innovate to overcome a challenge?

Innovation, for me, was mainly in the design stage where I wanted fragments in a main activity so I could have maximum modularity. My initial design, when first creating the UI, was to just have a separate activity for each screen, but doing research on fragments I decided that fragments were the correct way to go about implementing different functionalities, especially with a bottom navigation bar.

## In what specific component of your mobile app were you particularly successful in demonstrating your knowledge, skills, and experience?

I liked the way I implemented my backend database. The tables I created were minimal but allowed for full functionality. Each user in the login table had their goal weight saved, as I felt like creating yet another table just to hold a user id and a goal weight was wasteful. Additionally, Implementing the recycler view for my database items are another portion of the app that I am proud of. Recycler views were something I was intimidated by, and it took a decent chunk of time to understand them enough to implement them. I'm still not completely comfortable with them, but as I work with them further, I think I will be able to master them.
