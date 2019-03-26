package io.anuke.arc.backends.ios;

import io.anuke.arc.audio.Sound;
import io.anuke.arc.backends.ios.objectal.ALBuffer;
import io.anuke.arc.backends.ios.objectal.ALChannelSource;
import io.anuke.arc.backends.ios.objectal.ALSource;
import io.anuke.arc.backends.ios.objectal.OALSimpleAudio;
import io.anuke.arc.collection.IntArray;
import io.anuke.arc.files.FileHandle;
import org.robovm.apple.foundation.NSArray;

/**
 * @author tescott
 * @author Tomski
 * <p>
 * First pass at implementing OALSimpleAudio support.
 */
public class IOSSound implements Sound{

    private ALBuffer soundBuffer;
    private String soundPath;

    private ALChannelSource channel;
    private NSArray<ALSource> sourcePool;
    private IntArray streamIds = new IntArray(8);

    public IOSSound(FileHandle filePath){
        soundPath = filePath.file().getPath().replace('\\', '/');
        soundBuffer = OALSimpleAudio.sharedInstance().preloadEffect(soundPath);
        channel = OALSimpleAudio.sharedInstance().getChannelSource();
        sourcePool = channel.getSourcePool().getSources();
    }

    @Override
    public long play(){
        return play(1, 1, 0, false);
    }

    @Override
    public long play(float volume){
        return play(volume, 1, 0, false);
    }

    @Override
    public long play(float volume, float pitch, float pan){
        return play(volume, pitch, pan, false);
    }

    public long play(float volume, float pitch, float pan, boolean loop){
        if(streamIds.size == 8) streamIds.pop();
        ALSource soundSource = OALSimpleAudio.sharedInstance().playBuffer(soundBuffer, volume, pitch, pan, loop);
        if(soundSource == null) return -1;
        if(soundSource.getSourceId() == -1) return -1;
        streamIds.insert(0, soundSource.getSourceId());
        return soundSource.getSourceId();
    }

    @Override
    public long loop(){
        return play(1, 1, 0, true);
    }

    @Override
    public long loop(float volume){
        return play(volume, 1, 0, true);
    }

    @Override
    public long loop(float volume, float pitch, float pan){
        return play(volume, pitch, pan, true);
    }

    @Override
    public void stop(){
        ALSource source;
        for(int i = 0; i < streamIds.size; i++){
            if((source = getSoundSource(streamIds.get(i))) != null) source.stop();
        }
    }

    @Override
    public void dispose(){
        stop();
        OALSimpleAudio.sharedInstance().unloadEffect(soundPath);
    }

    @Override
    public void stop(long soundId){
        ALSource source;
        if((source = getSoundSource(soundId)) != null) source.stop();
    }

    @Override
    public void setLooping(long soundId, boolean looping){
        ALSource source;
        if((source = getSoundSource(soundId)) != null) source.setLooping(looping);
    }

    @Override
    public void setPitch(long soundId, float pitch){
        ALSource source;
        if((source = getSoundSource(soundId)) != null) source.setPitch(pitch);
    }

    @Override
    public void setVolume(long soundId, float volume){
        ALSource source;
        if((source = getSoundSource(soundId)) != null) source.setVolume(volume);
    }

    @Override
    public void setPan(long soundId, float pan, float volume){
        ALSource source;
        if((source = getSoundSource(soundId)) != null){
            source.setPan(pan);
            source.setVolume(volume);
        }
    }

    @Override
    public void pause(){
        ALSource source;
        for(int i = 0; i < streamIds.size; i++){
            if((source = getSoundSource(streamIds.get(i))) != null) source.setPaused(true);
        }
    }

    @Override
    public void resume(){
        ALSource source;
        for(int i = 0; i < streamIds.size; i++){
            if((source = getSoundSource(streamIds.get(i))) != null) source.setPaused(false);
        }
    }

    @Override
    public void pause(long soundId){
        ALSource source;
        if((source = getSoundSource(soundId)) != null) source.setPaused(true);
    }

    @Override
    public void resume(long soundId){
        ALSource source;
        if((source = getSoundSource(soundId)) != null) source.setPaused(false);
    }

    private ALSource getSoundSource(long soundId){
        for(ALSource source : sourcePool){
            if(source.getSourceId() == soundId) return source;
        }
        return null;
    }
}