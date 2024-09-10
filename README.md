# Car Park Finder (PostGis, Uber H3 Spatial Index)
System that allows users to find the nearest car parks based on their current location (latitude, longitude). The system should return a list of car parks sorted by distance from the user's location.

## Data Source
### Car park information
Dataset: [HDB Carpark Information](https://beta.data.gov.sg/datasets/d_23f946fa557947f93a8043bbef41dd09/view) \
Information about HDB carparks such as operating hours, car park location (in SVY21), type of parking system, etc.
> The Park and Ride Scheme has ceased with effect from 1 Dec 2016

### Car park availability
API Endpoint: https://api.data.gov.sg/v1/transport/carpark-availability \
This GOV public provides live updates on the parking lot availability for the car parks. It retrieved every minute.\
More information, please check [GOV API docs](https://data.gov.sg/datasets/d_ca933a644e55d34fe21f28b8052fac63/view)

### Proposed Solutions:

1. **Using PostGIS Extension:**
   - **Overview:** PostGIS is an extension for PostgreSQL that provides support for geographic data and spatial queries.
   - **Implementation:** Store geographic data for car parks in a PostgreSQL database with PostGIS enabled. Utilize PostGIS functions and spatial indexes to calculate distances between the user's location and car parks, and retrieve the nearest car parks efficiently.

2. **Using Uber H3 Index:**
   - **Overview:** Uber H3 is a geospatial indexing system that divides the world into hexagonal cells for location-based queries.
   - **Implementation:** Map car parks to H3 hexagons and use H3 algorithms to calculate distances and identify the nearest hexagons to the user's location. This approach enables efficient searching and retrieval of nearby car parks.

### Installation
> Require: Docker

- Modify `.env`
- Go to project folder
```cmd
docker-compose up
```
- Go to http://localhost:8080/api-docs/ui to access API docs to test
