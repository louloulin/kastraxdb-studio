import indexedDB from '@/indexedDB';

/** 初始化indexedDB */
const initIndexedDB = () => {
  indexedDB.createDB('magicdb', 1).then((db) => {
    window._indexedDB = {
      magicdb: db,
    };
  });
};

export default initIndexedDB;
