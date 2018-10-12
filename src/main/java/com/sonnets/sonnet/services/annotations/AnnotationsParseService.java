package com.sonnets.sonnet.services.annotations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sonnets.sonnet.persistence.models.annotation_types.Dialog;
import com.sonnets.sonnet.persistence.models.base.Annotation;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.repositories.DialogRepository;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import com.sonnets.sonnet.services.prose.CharacterService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * Deals with all the JSON parsing for rawJSON from the front-end and database.
 *
 * @author Josh Harkema
 */
@Service
@PropertySource("classpath:global.properties")
public class AnnotationsParseService {
    private static final Logger LOGGER = Logger.getLogger(AnnotationsParseService.class);
    private static final String SENTENCES = "sentences";
    private static final String ANNOTATIONS = "annotations";
    private final CharacterService characterService;
    private final DialogRepository dialogRepository;
    private final Environment env;

    @Autowired
    public AnnotationsParseService(CharacterService characterService, DialogRepository dialogRepository,
                                   Environment env) {
        this.characterService = characterService;
        this.dialogRepository = dialogRepository;
        this.env = env;
    }

    /**
     * Takes a raw JSON model from the front end and strips all the annotations into their respective objects and saves
     * them in the proper book character object.
     *
     * @param rawJSON the raw JSON string.
     * @param section the section to add the annotations to.
     * @return a section object with new annotations parsed into it.
     */
    public Section parseSectionAnnotations(final String rawJSON, Section section) {
        LOGGER.debug("Parsing annotations for section: " + section.getId());
        JSONObject jsonObject;
        JSONObject jsonObjectOut;
        JSONArray annotationsArray;
        JSONArray sentencesArray;
        try {
            jsonObject = new JSONObject(rawJSON);
            jsonObjectOut = new JSONObject();

            annotationsArray = getAnnotations(jsonObject);
            sentencesArray = jsonObject.getJSONArray(SENTENCES);

            // This checks to see if the annotations array exists.
            if (annotationsArray != null) {
                parseDialogAnnotations(annotationsArray, section.getId());
                BookCharacter narrator = parseNarratorAnnotation(annotationsArray);
                section.setNarrator(narrator);
            }

            jsonObjectOut.put(SENTENCES, sentencesArray);
            if (section.getAnnotation() == null) {
                section.setAnnotation(new Annotation());
            }

            section.getAnnotation().setAnnotationBody(jsonObjectOut.toString());
        } catch (JSONException e) {
            LOGGER.error(e);
            LOGGER.error("Escaping into the aether.");
            return getSentencesSolo(rawJSON, section);
        }
        return section;
    }

    /**
     * Catches parses errors for empty sentences arrays. Adds the sentences array to the section. Should not usually
     * return null.
     *
     * @param rawJSON the raw json with the sentences array.
     * @param section the section to parse the sentences onto.
     * @return null if an error or a section if successful.
     */
    private Section getSentencesSolo(String rawJSON, Section section) {
        JSONArray sentences;
        try {
            sentences = new JSONArray(rawJSON);
            section.getAnnotation().setAnnotationBody(sentences.toString());
        } catch (JSONException e) {
            LOGGER.error(e);
            return null;
        }
        return section;
    }

    /**
     * Returns a parsed JSONArray if the rawJSON contains an annotations object. Returns null if nothing is found.
     *
     * @param rawJSON the JSONObject to check.
     * @return null if an error, a valid JSONArray if successful.
     */
    private JSONArray getAnnotations(JSONObject rawJSON) {
        JSONArray annotationsArray;
        try {
            annotationsArray = rawJSON.getJSONArray(ANNOTATIONS);
        } catch (JSONException e) {
            LOGGER.error(e);
            return null;
        }
        return annotationsArray;
    }

    /**
     * Parses dialog objects from RAW JSON into database objects. Saves the dialog into the db's Dialog table, as
     * well as the respective character table.
     *
     * @param annotationsArray a loaded JSONArray of all the annotations.
     * @param sectionId        the id of the section the annotations came from
     * @throws JSONException not always a real error.
     */
    private void parseDialogAnnotations(final JSONArray annotationsArray, final Long sectionId) throws JSONException {
        for (int i = 0; i < annotationsArray.length(); i++) {
            JSONObject o = annotationsArray.getJSONObject(i);
            if (o.getString(env.getProperty("annotation.type")).equals(env.getProperty("annotation.type.character"))) {
                BookCharacter character = characterService.getCharacterOrThrowNotFound(
                        String.valueOf(o.getString(env.getProperty("annotation.itemId")))
                );
                Dialog dialog = this.loadDialogOrCreateNew(o);
                dialog.setItemFriendly(o.getString(env.getProperty("annotation.itemFriendly")));
                dialog.setItemId(Long.parseLong(o.getString(env.getProperty("annotation.itemId"))));
                dialog.setBody(o.getString(env.getProperty("annotation.body")));
                dialog.setCharacterOffsetBegin(o.getLong(env.getProperty("annotation.offsetBegin")));
                dialog.setCharacterOffsetEnd(o.getLong(env.getProperty("annotation.offsetEnd")));
                dialog.setSectionId(sectionId);
                dialog.setCreatedBy(o.getString(env.getProperty("auditor.createdBy"))); // :TODO: remove when migrated.
                dialogRepository.save(dialog);
                character.getDialog().add(dialog);
                characterService.save(character);
            }
        }
    }

    /**
     * Parses together related annotation objects into JSON that can be read by the front-end. This code was
     * a literal nightmare to write.
     *
     * @param section the section's annotations to parse into JSON.
     * @return a JSONObject containing all the annotations or null if any errors occur.
     */
    public JSONObject parseSectionAnnotationOut(final Section section) {
        LOGGER.debug("Parsing annotations to JSON for section: " + section.getId());
        Gson gson = new GsonBuilder().create();
        try {
            JSONObject out = new JSONObject();
            JSONObject in = new JSONObject(section.getAnnotation().getAnnotationBody());

            JSONArray sentencesArray = in.getJSONArray(SENTENCES);
            JSONArray annotationsArray = new JSONArray();

            // Parse in dialog objects from the DB (if any exist.)
            Set<Dialog> dialogSet = dialogRepository.findAllBySectionId(section.getId());
            for (Dialog dialog : dialogSet) {
                String json = gson.toJson(dialog);
                JSONObject o = new JSONObject(json);
                o.put(env.getProperty("annotation.type"), env.getProperty("annotation.type.character"));
                annotationsArray.put(o);
            }

            // Parse in a narrator object (if one exists.)
            if (section.getNarrator() != null) {
                BookCharacter narrator = section.getNarrator();
                JSONObject o = new JSONObject();
                o.put(env.getProperty("annotation.itemId"), narrator.getId());
                o.put(env.getProperty("annotation.type"), env.getProperty("annotation.type.narrator"));
                o.put(env.getProperty("annotation.itemFriendly"),
                        String.format("%s %s", narrator.getFirstName(), narrator.getLastName()));
                o.put(env.getProperty("auditor.createdBy"), narrator.getCreatedBy());
                o.put(env.getProperty("auditor.createdDate"), narrator.getCreatedDate());
                annotationsArray.put(o);
            }

            // Attach the arrays to the ouput object.
            out.put(SENTENCES, sentencesArray);
            out.put(ANNOTATIONS, annotationsArray);
            return out;
        } catch (JSONException e) {
            LOGGER.error(e);
        }
        return null;
    }

    /**
     * Pulls out Narrator annotations and returns it as a BookCharacter object.
     *
     * @param annotationsArray a loaded JSONArray containing all the annotations.
     * @return a loaded BookCharacter object if the annotations contain a narrator, null if nothing is found.
     * @throws JSONException if the annotationsArray cannot be parsed.
     */
    private BookCharacter parseNarratorAnnotation(final JSONArray annotationsArray) throws JSONException {
        LOGGER.debug("Parsing narrator annotations.");
        for (int i = 0; i < annotationsArray.length(); i++) {
            JSONObject o = annotationsArray.getJSONObject(i);
            if (o.getString(env.getProperty("annotation.type")).equals(env.getProperty("annotation.type.narrator"))) {
                return characterService.getCharacterOrThrowNotFound(o.getLong("itemId"));
            }
        }
        return null;
    }

    /**
     * Searches the database for an exising dialog with the same id, if one is found it is returned, if nothing is
     * found a new dialog is returned.
     *
     * @param o the raw annotation object.
     * @return an existing dialog (if found) or a new dialog (if nothing is found.)
     */
    private Dialog loadDialogOrCreateNew(JSONObject o) {
        try {
            if (dialogRepository.existsById(o.getLong(env.getProperty("annotation.id")))) {
                Optional<Dialog> dialog = dialogRepository.findById(o.getLong(env.getProperty("annotation.id")));
                return dialog.orElseThrow(ItemNotFoundException::new);
            }
        } catch (JSONException e) {
            LOGGER.error(e);
        }
        return new Dialog();
    }
}
