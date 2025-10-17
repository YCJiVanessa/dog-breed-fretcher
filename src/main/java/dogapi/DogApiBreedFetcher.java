package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        final String url = "https://dog.ceo/api/breed/" + breed.toLowerCase() + "/list";
        Request request = new Request.Builder().url(url).build();
        List<String> breedsList;
        try {
            Response response = client.newCall(request).execute();
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject Json_data = new JSONObject(responseBody);
            breedsList = new ArrayList<>();
            if (Objects.equals(Json_data.getString("status"), "success")) {
                JSONArray sub_breeds = new JSONArray(Json_data.getJSONArray("message"));
                for (int i = 0; i < sub_breeds.length(); i++) {
                    String single_breed = sub_breeds.getString(i);
                    breedsList.add(single_breed);
                }
            }
            else {
                throw new BreedNotFoundException("Failed to retrieve data from API" + breed);

            }
        } catch (IOException e) {
            throw new BreedNotFoundException("Failed to fetch sub-breeds for " + breed);
        }
        return breedsList;
    }
}
