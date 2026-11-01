import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioManager {
    private enum Sound {
        MENU_MUSIC("assets/audio/menu_music.wav"),
        SIMULATION_MUSIC("assets/audio/simulation_music.wav"),
        COLLECT_DNA("assets/audio/collect_dna.wav"),
        PAUSE("assets/audio/pause.wav"),
        RESET("assets/audio/reset.wav");

        private final String path;

        Sound(String path) {
            this.path = path;
        }
    }

    private final Map<Sound, Clip> musicClips = new EnumMap<>(Sound.class);
    private final Map<Sound, Boolean> missingSounds = new EnumMap<>(Sound.class);
    private Clip activeMusic;

    public void startMenuMusic() {
        playMusic(Sound.MENU_MUSIC);
    }

    public void startSimulationMusic() {
        playMusic(Sound.SIMULATION_MUSIC);
    }

    public void playDnaCollected() {
        playEffect(Sound.COLLECT_DNA);
    }

    public void playPause() {
        playEffect(Sound.PAUSE);
    }

    public void playReset() {
        playEffect(Sound.RESET);
    }

    public void stop() {
        if (activeMusic != null) {
            activeMusic.stop();
            activeMusic.close();
            activeMusic = null;
        }

        for (Clip clip : musicClips.values()) {
            if (clip != activeMusic) {
                clip.close();
            }
        }
        musicClips.clear();
    }

    private void playMusic(Sound sound) {
        if (activeMusic != null && activeMusic == getMusicClip(sound)) {
            if (!activeMusic.isRunning()) {
                activeMusic.loop(Clip.LOOP_CONTINUOUSLY);
                activeMusic.start();
            }
            return;
        }

        if (activeMusic != null) {
            activeMusic.stop();
        }

        Clip clip = getMusicClip(sound);
        if (clip == null) {
            return;
        }

        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
        activeMusic = clip;
    }

    private Clip getMusicClip(Sound sound) {
        Clip existingClip = musicClips.get(sound);
        if (existingClip != null) {
            return existingClip;
        }

        Clip loadedClip = loadClip(sound);
        if (loadedClip != null) {
            musicClips.put(sound, loadedClip);
        }
        return loadedClip;
    }

    private void playEffect(Sound sound) {
        Clip clip = loadClip(sound);
        if (clip == null) {
            return;
        }

        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                clip.close();
            }
        });
        clip.start();
    }

    private Clip loadClip(Sound sound) {
        File audioFile = resolveAudioFile(sound.path);
        if (!audioFile.isFile()) {
            if (!missingSounds.containsKey(sound)) {
                missingSounds.put(sound, true);
                System.err.println("Audio file not found. Checked: "
                        + new File(sound.path).getPath() + " and " + new File("Indemic", sound.path).getPath());
            }
            return null;
        }

        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException exception) {
            System.err.println("Unable to load audio file " + audioFile.getPath() + ": "
                    + exception.getMessage());
            return null;
        }
    }

    private File resolveAudioFile(String relativePath) {
        File localFile = new File(relativePath);
        if (localFile.isFile()) {
            return localFile;
        }

        return new File("Indemic", relativePath);
    }
}
