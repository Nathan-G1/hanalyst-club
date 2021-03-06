package hanalyst.application.hanalystclub.repository;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import hanalyst.application.hanalystclub.Database.HAnalystDb;
import hanalyst.application.hanalystclub.Entity.Notation;
import hanalyst.application.hanalystclub.dao.NotationDao;

public class NotationRepository {
    private NotationDao notationDao;
    private LiveData<List<Notation>> allNotations;

    public NotationRepository(Application application) {
        HAnalystDb hAnalystDb = HAnalystDb.getInstance(application);
        notationDao = hAnalystDb.notationDao();
        allNotations = notationDao.getAllNotations();
    }

    public void insert(Notation notation) {
        new InsertNotationAsyncTask(notationDao).execute(notation);
    }

    public LiveData<List<Notation>> getAllNotations() {
        return allNotations;
    }

    public void updateNotation(Notation notation) {
        new UpdateNotationAsyncTask(notationDao).execute(notation);
    }

    public void deleteNotation(Notation notation) {
        new DeleteNotationAsyncTask(notationDao).execute(notation);
    }

    public void deleteAllNotations() {
        new DeleteAllNotationsAsyncTask(notationDao).execute();
    }


    private class InsertNotationAsyncTask extends AsyncTask<Notation, Void, Void> {
        private NotationDao notationDao;

        private InsertNotationAsyncTask(NotationDao notationDao) {
            this.notationDao = notationDao;
        }

        @Override
        protected Void doInBackground(Notation... notations) {
            notationDao.insertNotation(notations[0]);
            return null;
        }
    }

    private static class UpdateNotationAsyncTask extends AsyncTask<Notation, Void, Void> {

        private NotationDao notationDao;

        private UpdateNotationAsyncTask(NotationDao notationDao) {
            this.notationDao = notationDao;
        }

        @Override
        protected Void doInBackground(Notation... notations) {
            notationDao.updateNotation(notations[0]);
            return null;
        }
    }

    private static class DeleteNotationAsyncTask extends AsyncTask<Notation, Void, Void> {

        private NotationDao notationDao;

        private DeleteNotationAsyncTask(NotationDao notationDao) {
            this.notationDao = notationDao;
        }

        @Override
        protected Void doInBackground(Notation... notations) {
            notationDao.deleteNotation(notations[0]);
            return null;
        }
    }

    private class DeleteAllNotationsAsyncTask extends AsyncTask<Void, Void, Void> {
        private NotationDao notationDao;

        public DeleteAllNotationsAsyncTask(NotationDao notationDao) {
            this.notationDao = notationDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            notationDao.deleteAllNotations();
            return null;
        }
    }
}
