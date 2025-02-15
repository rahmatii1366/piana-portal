this is where it is responsible for loading endpoints from the database and checking for endpoint changes.
after running this project, it first loads the configuration from database then places it in the redis cache.
from here on, it schedules the job to check for endpoint changes, at specified intervals.