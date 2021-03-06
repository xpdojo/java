package org.xpdojo.concurrency.pragmatic;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.xpdojo.concurrency.pragmatic.Answer;
import org.xpdojo.concurrency.pragmatic.Bool;
import org.xpdojo.concurrency.pragmatic.BooleanQuestion;
import org.xpdojo.concurrency.pragmatic.Criteria;
import org.xpdojo.concurrency.pragmatic.Criterion;
import org.xpdojo.concurrency.pragmatic.MatchListener;
import org.xpdojo.concurrency.pragmatic.MatchSet;
import org.xpdojo.concurrency.pragmatic.Profile;
import org.xpdojo.concurrency.pragmatic.ProfileMatcher;
import org.xpdojo.concurrency.pragmatic.Weight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Pragmatic Unit Testing in Java 8 with JUnit
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfileMatcherTest {
    private BooleanQuestion question;
    private Criteria criteria;
    private ProfileMatcher matcher;
    private Profile matchingProfile;
    private Profile nonMatchingProfile;

    @BeforeAll
    public void create() {
        question = new BooleanQuestion(1, "");
        criteria = new Criteria();
        criteria.add(new Criterion(matchingAnswer(), Weight.MustMatch));
        matchingProfile = createMatchingProfile("matching");
        nonMatchingProfile = createNonMatchingProfile("nonMatching");
    }

    private Profile createMatchingProfile(String name) {
        Profile profile = new Profile(name);
        profile.add(matchingAnswer());
        return profile;
    }

    private Profile createNonMatchingProfile(String name) {
        Profile profile = new Profile(name);
        profile.add(nonMatchingAnswer());
        return profile;
    }

    private Answer matchingAnswer() {
        return new Answer(question, Bool.TRUE);
    }

    private Answer nonMatchingAnswer() {
        return new Answer(question, Bool.FALSE);
    }

    @BeforeAll
    public void createMatcher() {
        matcher = new ProfileMatcher();
    }

    @Test
    @DisplayName("collectMatchSets()")
    void collectsMatchSets() {
        matcher.add(matchingProfile);
        matcher.add(nonMatchingProfile);

        Set<String> actual = matcher.collectMatchSets(criteria)
                .stream()
                .map(MatchSet::getProfileId)
                .collect(Collectors.toSet());

        assertThat(actual)
                .containsExactlyInAnyOrder(matchingProfile.getId(), nonMatchingProfile.getId());
    }

    private MatchListener listener;

    @BeforeAll
    public void createMatchListener() {
        listener = mock(MatchListener.class);
    }

    @Test
    @DisplayName("matchingProfile??? ?????? matcher.process()")
    void processNotifiesListenerOnMatch() {
        // ????????? ????????? ????????? ????????? ???????????? Profile??? matcher ????????? ???????????????.
        matcher.add(matchingProfile);

        // ????????? ?????? ????????? ???????????? Profile??? ?????? MatchSet ????????? ???????????????.
        MatchSet matchSet = matchingProfile.getMatchSet(criteria);

        // mocking listener??? MatchSet ????????? ?????? ?????? ????????? ???????????????.
        matcher.process(listener, matchSet);

        verify(listener)
                .foundMatch(matchingProfile, matchSet);
    }

    @Test
    @DisplayName("nonMatchingProfile??? ?????? matcher.process()")
    void processDoesNotNotifyListenerWhenNoMatch() {
        matcher.add(nonMatchingProfile);

        MatchSet matchSet = nonMatchingProfile.getMatchSet(criteria);

        matcher.process(listener, matchSet);

        verify(listener, never())
                .foundMatch(nonMatchingProfile, matchSet);
    }

    @Test
    @DisplayName("Multi Thread")
    void gathersMatchingProfiles() {

        // Listener??? ???????????? MatchSet ???????????? ???????????? ID ?????????
        // ????????? ????????? Set ????????? ???????????????.
        Set<String> processedProfileIds = Collections.synchronizedSet(new HashSet<>());

        // ???????????? MatchSet ???????????? ???????????????.
        List<MatchSet> matchSets = createMatchSets(100);

        matcher.findMatchingProfiles(
                listener, // mocking
                matchSets,
                (listener, matchSet) -> {
                    // ???????????? ?????? ??? ????????????
                    // MatchSet ????????? ProfileId??? ????????????.
                    processedProfileIds.add(matchSet.getProfileId());
                });

        while (!matcher.getExecutor().isTerminated()) {
            // matcher?????? ExecutorService ????????? ?????? ??????
            // ?????? ???????????? ????????? ????????? ????????? ???????????? ???????????????.
        }

        Set<String> profileIds = matchSets.stream()
                .map(MatchSet::getProfileId)
                .collect(Collectors.toSet());

        assertThat(processedProfileIds)
                .isEqualTo(profileIds);
    }

    private List<MatchSet> createMatchSets(int count) {
        List<MatchSet> sets = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            sets.add(new MatchSet(String.valueOf(i), null, null));
        }

        return sets;
    }
}
